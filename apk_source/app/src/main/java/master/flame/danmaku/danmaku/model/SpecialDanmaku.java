package master.flame.danmaku.danmaku.model;

/* loaded from: classes.dex */
public class SpecialDanmaku extends BaseDanmaku {
    public long alphaDuration;
    public int beginAlpha;
    public float beginX;
    public float beginY;
    private float[] currStateValues = new float[4];
    public int deltaAlpha;
    public float deltaX;
    public float deltaY;
    public int endAlpha;
    public float endX;
    public float endY;
    public LinePath[] linePaths;
    public float pivotX;
    public float pivotY;
    public float rotateX;
    public float rotateZ;
    public long translationDuration;
    public long translationStartDelay;

    private class Point {
        float x;
        float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getDistance(Point p) {
            float _x = Math.abs(this.x - p.x);
            float _y = Math.abs(this.y - p.y);
            return (float) Math.sqrt((_x * _x) + (_y * _y));
        }
    }

    public class LinePath {
        public long beginTime;
        float delatX;
        float deltaY;
        public long duration;
        public long endTime;
        Point pBegin;
        Point pEnd;

        public LinePath() {
        }

        public void setPoints(Point pBegin, Point pEnd) {
            this.pBegin = pBegin;
            this.pEnd = pEnd;
            this.delatX = pEnd.x - pBegin.x;
            this.deltaY = pEnd.y - pBegin.y;
        }

        public float getDistance() {
            return this.pEnd.getDistance(this.pBegin);
        }

        public float[] getBeginPoint() {
            return new float[]{this.pBegin.x, this.pBegin.y};
        }

        public float[] getEndPoint() {
            return new float[]{this.pEnd.x, this.pEnd.y};
        }
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public void layout(IDisplayer displayer, float x, float y) {
        getRectAtTime(displayer, this.mTimer.currMillisecond);
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float[] getRectAtTime(IDisplayer displayer, long currTime) {
        if (!isMeasured()) {
            return null;
        }
        long deltaTime = currTime - this.time;
        if (this.alphaDuration > 0 && this.deltaAlpha != 0) {
            if (deltaTime >= this.alphaDuration) {
                this.alpha = this.endAlpha;
            } else {
                float alphaProgress = deltaTime / this.alphaDuration;
                int vectorAlpha = (int) (this.deltaAlpha * alphaProgress);
                this.alpha = this.beginAlpha + vectorAlpha;
            }
        }
        float currX = this.beginX;
        float currY = this.beginY;
        long dtime = deltaTime - this.translationStartDelay;
        if (this.translationDuration > 0 && dtime >= 0 && dtime <= this.translationDuration) {
            float tranalationProgress = dtime / this.translationDuration;
            if (this.linePaths != null) {
                LinePath currentLinePath = null;
                LinePath[] linePathArr = this.linePaths;
                int length = linePathArr.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        LinePath line = linePathArr[i];
                        if (dtime >= line.beginTime && dtime < line.endTime) {
                            currentLinePath = line;
                            break;
                        }
                        currX = line.pEnd.x;
                        currY = line.pEnd.y;
                        i++;
                    } else {
                        break;
                    }
                }
                if (currentLinePath != null) {
                    float deltaX = currentLinePath.delatX;
                    float deltaY = currentLinePath.deltaY;
                    float tranalationProgress2 = (deltaTime - currentLinePath.beginTime) / currentLinePath.duration;
                    float beginX = currentLinePath.pBegin.x;
                    float beginY = currentLinePath.pBegin.y;
                    if (deltaX != 0.0f) {
                        float vectorX = deltaX * tranalationProgress2;
                        currX = beginX + vectorX;
                    }
                    if (deltaY != 0.0f) {
                        float vectorY = deltaY * tranalationProgress2;
                        currY = beginY + vectorY;
                    }
                }
            } else {
                if (this.deltaX != 0.0f) {
                    float vectorX2 = this.deltaX * tranalationProgress;
                    currX = this.beginX + vectorX2;
                }
                if (this.deltaY != 0.0f) {
                    float vectorY2 = this.deltaY * tranalationProgress;
                    currY = this.beginY + vectorY2;
                }
            }
        } else if (dtime > this.translationDuration) {
            currX = this.endX;
            currY = this.endY;
        }
        this.currStateValues[0] = currX;
        this.currStateValues[1] = currY;
        this.currStateValues[2] = this.paintWidth + currX;
        this.currStateValues[3] = this.paintHeight + currY;
        setVisibility(!isOutside());
        return this.currStateValues;
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getLeft() {
        return this.currStateValues[0];
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getTop() {
        return this.currStateValues[1];
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getRight() {
        return this.currStateValues[2];
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public float getBottom() {
        return this.currStateValues[3];
    }

    @Override // master.flame.danmaku.danmaku.model.BaseDanmaku
    public int getType() {
        return 7;
    }

    public void setTranslationData(float beginX, float beginY, float endX, float endY, long translationDuration, long translationStartDelay) {
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
        this.deltaX = endX - beginX;
        this.deltaY = endY - beginY;
        this.translationDuration = translationDuration;
        this.translationStartDelay = translationStartDelay;
    }

    public void setAlphaData(int beginAlpha, int endAlpha, long alphaDuration) {
        this.beginAlpha = beginAlpha;
        this.endAlpha = endAlpha;
        this.deltaAlpha = endAlpha - beginAlpha;
        this.alphaDuration = alphaDuration;
        if (this.deltaAlpha != 0 && beginAlpha != AlphaValue.MAX) {
            this.alpha = beginAlpha;
        }
    }

    public void setLinePathData(float[][] points) {
        if (points != null) {
            int length = points.length;
            this.beginX = points[0][0];
            this.beginY = points[0][1];
            this.endX = points[length - 1][0];
            this.endY = points[length - 1][1];
            if (points.length > 1) {
                this.linePaths = new LinePath[points.length - 1];
                for (int i = 0; i < this.linePaths.length; i++) {
                    this.linePaths[i] = new LinePath();
                    this.linePaths[i].setPoints(new Point(points[i][0], points[i][1]), new Point(points[i + 1][0], points[i + 1][1]));
                }
                float totalDistance = 0.0f;
                for (LinePath linePath : this.linePaths) {
                    totalDistance += linePath.getDistance();
                }
                LinePath lastLine = null;
                for (LinePath line : this.linePaths) {
                    line.duration = (long) ((line.getDistance() / totalDistance) * this.translationDuration);
                    line.beginTime = lastLine == null ? 0L : lastLine.endTime;
                    line.endTime = line.beginTime + line.duration;
                    lastLine = line;
                }
            }
        }
    }

    public void updateData(float scale) {
    }
}
