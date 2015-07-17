/*
 * Copyright (C) 2013 Chen Hui <calmer91@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package master.flame.danmaku.controller;

import android.content.Context;
import android.graphics.Canvas;

import master.flame.danmaku.danmaku.model.AbsDisplayer;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.GlobalFlagValues;
import master.flame.danmaku.danmaku.model.IDanmakuIterator;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig.DanmakuConfigTag;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuGlobalConfig.ConfigChangedCallback;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.renderer.IRenderer;
import master.flame.danmaku.danmaku.renderer.IRenderer.RenderingState;
import master.flame.danmaku.danmaku.renderer.android.DanmakuRenderer;
import master.flame.danmaku.danmaku.renderer.android.DanmakusRetainer;

public class DrawTask implements IDrawTask, ConfigChangedCallback {
    
    protected AbsDisplayer<?> mDisp;

    protected IDanmakus danmakuList;

    protected BaseDanmakuParser mParser;

    TaskListener mTaskListener;

    Context mContext;

    IRenderer mRenderer;

    DanmakuTimer mTimer;

    private IDanmakus danmakus = new Danmakus(Danmakus.ST_BY_LIST);

    protected boolean clearRetainerFlag;

    private long mStartRenderTime = 0;

    private RenderingState mRenderingState = new RenderingState();

    protected boolean mReadyState;

    private long mLastBeginMills;

    private long mLastEndMills;

    private boolean mIsHidden;

    public DrawTask(DanmakuTimer timer, Context context, AbsDisplayer<?> disp,
            TaskListener taskListener) {
        mTaskListener = taskListener;
        mContext = context;
        mRenderer = new DanmakuRenderer();
        mDisp = disp;
        initTimer(timer);
        Boolean enable = DanmakuGlobalConfig.DEFAULT.isDuplicateMergingEnabled();
        if (enable != null) {
            if(enable) {
                DanmakuFilters.getDefault().registerFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
            } else {
                DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
            }
        }
    }

    protected void initTimer(DanmakuTimer timer) {
        mTimer = timer;
    }

    @Override
    public void addDanmaku(BaseDanmaku item) {
        if (danmakuList == null)
            return;
        boolean added = false;
        synchronized (danmakuList) {
            if(item.isLive) {
                removeUnusedLiveDanmakusIn(10);
            }
            item.index = danmakuList.size();
            if (mLastBeginMills <= item.time && item.time <= mLastEndMills) {
                synchronized (danmakus) {
                    added = danmakus.addItem(item);
                }
            } else if(item.isLive){
                mLastBeginMills = mLastEndMills = 0;
            }
            added = danmakuList.addItem(item);
        }
        if (added && mTaskListener != null) {
            mTaskListener.onDanmakuAdd(item);
        }
    }
    
    @Override
    public void removeAllDanmakus() {
        if (danmakuList == null || danmakuList.isEmpty())
            return;
        synchronized (danmakuList) {
            danmakuList.clear();
        }
    }

    @Override
    public void removeAllLiveDanmakus() {
        if (danmakus == null || danmakus.isEmpty())
            return;
        synchronized (danmakus) {
            IDanmakuIterator it = danmakus.iterator();
            while (it.hasNext()) {
                if (it.next().isLive) {
                    it.remove();
                }
            }
        }
    }
    
    protected void removeUnusedLiveDanmakusIn(int msec) {
        if (danmakuList == null || danmakuList.isEmpty())
            return;
        synchronized (danmakuList) {
            long startTime = System.currentTimeMillis();
            IDanmakuIterator it = danmakuList.iterator();
            while (it.hasNext()) {
                BaseDanmaku danmaku = it.next();
                boolean isTimeout = danmaku.isTimeOut();
                if (isTimeout && danmaku.isLive) {
                    it.remove();
                }
                if (!isTimeout || System.currentTimeMillis() - startTime > msec) {
                    break;
                }
            }
        }
    }

    @Override
    public RenderingState draw(AbsDisplayer<?> displayer) {
        return drawDanmakus(displayer,mTimer);
    }

    @Override
    public void reset() {
        if (danmakus != null)
            danmakus.clear();
        if (mRenderer != null)
            mRenderer.clear();
    }

    @Override
    public void seek(long mills) {
        reset();
//        requestClear();
        GlobalFlagValues.updateVisibleFlag();
        mStartRenderTime = mills < 1000 ? 0 : mills;
    }

    @Override
    public void clearDanmakusOnScreen(long currMillis) {
        reset();
        GlobalFlagValues.updateVisibleFlag();
        mStartRenderTime = currMillis;
    }

    @Override
    public void start() {
        DanmakuGlobalConfig.DEFAULT.registerConfigChangedCallback(this);
    }

    @Override
    public void quit() {
        if (mRenderer != null)
            mRenderer.release();
        DanmakuGlobalConfig.DEFAULT.unregisterConfigChangedCallback(this);
    }

    public void prepare() {
        assert (mParser != null);
        loadDanmakus(mParser);
        if (mTaskListener != null) {
            mTaskListener.ready();
            mReadyState = true;
        }
    }

    protected void loadDanmakus(BaseDanmakuParser parser) {
        danmakuList = parser.setDisplayer(mDisp).setTimer(mTimer).getDanmakus();
        GlobalFlagValues.resetAll();
    }

    public void setParser(BaseDanmakuParser parser) {
        mParser = parser;
        mReadyState = false;
    }

    protected RenderingState drawDanmakus(AbsDisplayer<?> disp, DanmakuTimer timer) {
        if (clearRetainerFlag) {
            DanmakusRetainer.clear();
            clearRetainerFlag = false;
        }
        if (danmakuList != null) {
            Canvas canvas = (Canvas) disp.getExtraData();
            DrawHelper.clearCanvas(canvas);
            if (mIsHidden) {
                return mRenderingState;
            }
            long beginMills = timer.currMillisecond - DanmakuFactory.MAX_DANMAKU_DURATION - 100;
            long endMills = timer.currMillisecond + DanmakuFactory.MAX_DANMAKU_DURATION;
            if(mLastBeginMills > beginMills || timer.currMillisecond > mLastEndMills) {
                IDanmakus subDanmakus = danmakuList.sub(beginMills, endMills);
                if(subDanmakus != null) {
                    danmakus = subDanmakus;
                } else {
                    danmakus.clear();
                }
                mLastBeginMills = beginMills;
                mLastEndMills = endMills;
            } else {
                beginMills = mLastBeginMills;
                endMills = mLastEndMills;
            }
            if (danmakus != null && !danmakus.isEmpty()) {
                RenderingState renderingState = mRenderingState = mRenderer.draw(mDisp, danmakus, mStartRenderTime);
                if (renderingState.nothingRendered) {
                    if (renderingState.beginTime == RenderingState.UNKNOWN_TIME) {
                        renderingState.beginTime = beginMills;
                    }
                    if (renderingState.endTime == RenderingState.UNKNOWN_TIME) {
                        renderingState.endTime = endMills;
                    }
                }
                return renderingState;
            } else {
                mRenderingState.nothingRendered = true;
                mRenderingState.beginTime = beginMills;
                mRenderingState.endTime = endMills;
                return mRenderingState;
            }
        }
        return null;
    }

    public void requestClear() {
        mLastBeginMills = mLastEndMills = 0;
        mIsHidden = false;
    }

    public void requestClearRetainer() {
        clearRetainerFlag = true;
    }

    @Override
    public boolean onDanmakuConfigChanged(DanmakuGlobalConfig config, DanmakuConfigTag tag,
            Object... values) {
        boolean handled = handleOnDanmakuConfigChanged(config, tag, values);
        if (mTaskListener != null) {
            mTaskListener.onDanmakuConfigChanged();
        }
        return handled;
    }

    protected boolean handleOnDanmakuConfigChanged(DanmakuGlobalConfig config, DanmakuConfigTag tag, Object[] values) {
        boolean handled = false;
        if (tag == null || DanmakuConfigTag.MAXIMUM_NUMS_IN_SCREEN.equals(tag)) {
            handled = true;
        } else if (DanmakuConfigTag.DUPLICATE_MERGING_ENABLED.equals(tag)) {
            Boolean enable = (Boolean) values[0];
            if (enable != null) {
                if (enable) {
                    DanmakuFilters.getDefault().registerFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
                } else {
                    DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
                }
                handled = true;
            }
        } else if (DanmakuConfigTag.SCALE_TEXTSIZE.equals(tag) || DanmakuConfigTag.SCROLL_SPEED_FACTOR.equals(tag)) {
            requestClearRetainer();
            handled = false;
        }
        return handled;
    }

    @Override
    public void requestHide() {
        mIsHidden = true;
    }

}
