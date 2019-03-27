@file:Suppress("DEPRECATION")

package com.ltei.lauverticaldrawer

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.VelocityTrackerCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ScrollerCompat
import android.view.*
import android.view.animation.Interpolator
import java.util.*

/**
 * Apps should use ViewDragHelper.create() to get a new instance.
 * This will allow VDH to use internal compatibility implementations for different
 * platform versions.
 * If the interpolator is null, the default interpolator will be used.
 *
 * @param context Context to initialize config-dependent params from
 * @param forParent Parent view to monitor
 * @param interpolator interpolator for scroller
 */
open class ViewDragHelper private constructor(context: Context, forParent: ViewGroup?, interpolator: Interpolator?, cb: Callback?) {

    // Current drag state; idle, dragging or settling
    private var mDragState: Int = 0

    // Distance to travel before a drag may begin
    private var mTouchSlop: Int = 0

    // Last known position/pointer tracking
    private var mActivePointerId = INVALID_POINTER
    private var mInitialMotionX: FloatArray? = null
    private var mInitialMotionY: FloatArray? = null
    private var mLastMotionX: FloatArray? = null
    private var mLastMotionY: FloatArray? = null
    private var mInitialEdgesTouched: IntArray? = null
    private var mEdgeDragsInProgress: IntArray? = null
    private var mEdgeDragsLocked: IntArray? = null
    private var mPointersDown: Int = 0

    private var mVelocityTracker: VelocityTracker? = null
    private var mMaxVelocity: Float = 0.toFloat()
    private var mMinVelocity: Float = 0.toFloat()

    private var mEdgeSize: Int = 0
    private var mTrackingEdges: Int = 0

    private var mScroller: ScrollerCompat? = null

    private var mCallback: Callback? = cb

    private var mCapturedView: View? = null
    private var mReleaseInProgress: Boolean = false

    private var mParentView: ViewGroup? = forParent


    /**
     * A Callback is used as a communication channel with the ViewDragHelper back to the
     * parent view using it. `on*`methods are invoked on siginficant events and several
     * accessor methods are expected to provide the ViewDragHelper with more information
     * about the state of the parent view upon request. The callback also makes decisions
     * governing the range and draggability of child views.
     */
    interface Callback {
        /**
         * Called when the drag state changes. See the `STATE_*` constants
         * for more information.
         * @param state The new drag state
         * @see .STATE_IDLE .STATE_DRAGGING .STATE_SETTLING
         */
        fun onViewDragStateChanged(state: Int) {}

        /**
         * Called when the captured view's position changes as the result of a drag or settle.
         * @param changedView View whose position changed
         * @param left New X coordinate of the left edge of the view
         * @param top New Y coordinate of the top edge of the view
         * @param dx Change in X position from the last call
         * @param dy Change in Y position from the last call
         */
        fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {}

        /**
         * Called when a child view is captured for dragging or settling. The ID of the pointer
         * currently dragging the captured view is supplied. If activePointerId is
         * identified as [.INVALID_POINTER] the capture is programmatic instead of
         * pointer-initiated.
         * @param capturedChild Child view that was captured
         * @param activePointerId Pointer id tracking the child capture
         */
        fun onViewCaptured(capturedChild: View, activePointerId: Int) {}

        /**
         * Called when the child view is no longer being actively dragged.
         * The fling velocity is also supplied, if relevant. The velocity values may
         * be clamped to system minimums or maximums.
         * Calling code may decide to fling or otherwise release the view to let it
         * settle into place. It should do so using [.settleCapturedViewAt]
         * or [.flingCapturedView]. If the Callback invokes
         * one of these methods, the ViewDragHelper will enter [.STATE_SETTLING]
         * and the view capture will not fully end until it comes to a complete stop.
         * If neither of these methods is invoked before `onViewReleased` returns,
         * the view will stop in place and the ViewDragHelper will return to
         * [.STATE_IDLE].
         * @param releasedChild The captured child view now being released
         * @param xvel X velocity of the pointer as it left the screen in pixels per second.
         * @param yvel Y velocity of the pointer as it left the screen in pixels per second.
         */
        fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {}

        /**
         * Called when one of the subscribed edges in the parent view has been touched
         * by the user while no child view is currently captured.
         * @param edgeFlags A combination of edge flags describing the edge(s) currently touched
         * @param pointerId ID of the pointer touching the described edge(s)
         * @see .EDGE_LEFT .EDGE_TOP .EDGE_RIGHT .EDGE_BOTTOM
         */
        fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {}

        /**
         * Called when the given edge may become locked. This can happen if an edge drag
         * was preliminarily rejected before beginning, but after [.onEdgeTouched]
         * was called. This method should return true to lock this edge or false to leave it
         * unlocked. The default behavior is to leave edges unlocked.
         * @param edgeFlags A combination of edge flags describing the edge(s) locked
         * @return true to lock the edge, false to leave it unlocked
         */
        fun onEdgeLock(edgeFlags: Int): Boolean {
            return false
        }

        /**
         * Called when the user has started a deliberate drag away from one
         * of the subscribed edges in the parent view while no child view is currently captured.
         * @param edgeFlags A combination of edge flags describing the edge(s) dragged
         * @param pointerId ID of the pointer touching the described edge(s)
         * @see .EDGE_LEFT .EDGE_TOP .EDGE_RIGHT .EDGE_BOTTOM
         */
        fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {}

        /**
         * Called to determine the Z-order of child views.
         * @param index the ordered position to query for
         * @return index of the view that should be ordered at position `index`
         */
        fun getOrderedChildIndex(index: Int): Int {
            return index
        }

        /**
         * Return the magnitude of a draggable child view's horizontal range of motion in pixels.
         * This method should return 0 for views that cannot move horizontally.
         * @param child Child view to check
         * @return range of horizontal motion in pixels
         */
        fun getViewHorizontalDragRange(child: View): Int {
            return 0
        }

        /**
         * Return the magnitude of a draggable child view's vertical range of motion in pixels.
         * This method should return 0 for views that cannot move vertically.
         * @param child Child view to check
         * @return range of vertical motion in pixels
         */
        fun getViewVerticalDragRange(child: View): Int {
            return 0
        }

        /**
         * Called when the user's input indicates that they want to capture the given child view
         * with the pointer indicated by pointerId. The callback should return true if the user
         * is permitted to drag the given view with the indicated pointer.
         * ViewDragHelper may call this method multiple times for the same view even if
         * the view is already captured; this indicates that a new pointer is trying to take
         * control of the view.
         * If this method returns true, a call to [.onViewCaptured]
         * will follow if the capture is successful.
         * @param child Child the user is attempting to capture
         * @param pointerId ID of the pointer attempting the capture
         * @return true if capture should be allowed, false otherwise
         */
        fun tryCaptureView(child: View, pointerId: Int): Boolean

        /**
         * Restrict the motion of the dragged child view along the horizontal axis.
         * The default implementation does not allow horizontal motion; the extending
         * class must override this method and provide the desired clamping.
         * @param child Child view being dragged
         * @param left Attempted motion along the X axis
         * @param dx Proposed change in position for left
         * @return The new clamped position for left
         */
        fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return 0
        }

        /**
         * Restrict the motion of the dragged child view along the vertical axis.
         * The default implementation does not allow vertical motion; the extending
         * class must override this method and provide the desired clamping.
         * @param child Child view being dragged
         * @param top Attempted motion along the Y axis
         * @param dy Proposed change in position for top
         * @return The new clamped position for top
         */
        fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return 0
        }
    }

    /**
     * Interpolator defining the animation curve for mScroller
     */
    private val sInterpolator = Interpolator { input ->
        val tmp = input - 1f
        tmp * tmp * tmp * tmp * tmp + 1.0f
    }

    private val mSetIdleRunnable = Runnable { setDragState(STATE_IDLE) }

    init {
        if (forParent == null) {
            throw IllegalArgumentException("Parent view may not be null")
        }
        if (cb == null) {
            throw IllegalArgumentException("Callback may not be null")
        }
        val vc = ViewConfiguration.get(context)
        val density = context.resources.displayMetrics.density
        mEdgeSize = (EDGE_SIZE * density + 0.5f).toInt()
        mTouchSlop = vc.scaledTouchSlop
        mMaxVelocity = vc.scaledMaximumFlingVelocity.toFloat()
        mMinVelocity = vc.scaledMinimumFlingVelocity.toFloat()
        mScroller = ScrollerCompat.create(context, interpolator ?: sInterpolator)
    }

    /**
     * Set the minimum velocity that will be detected as having a magnitude greater than zero
     * in pixels per second. Callback methods accepting a velocity will be clamped appropriately.
     *
     * @param minVel Minimum velocity to detect
     */
    fun setMinVelocity(minVel: Float) {
        mMinVelocity = minVel
    }

    /**
     * Return the currently configured minimum velocity. Any flings with a magnitude less
     * than this value in pixels per second. Callback methods accepting a velocity will receive
     * zero as a velocity value if the real detected velocity was below this threshold.
     *
     * @return the minimum velocity that will be detected
     */
    fun getMinVelocity(): Float {
        return mMinVelocity
    }

    /**
     * Retrieve the current drag state of this helper. This will return one of
     * [.STATE_IDLE], [.STATE_DRAGGING] or [.STATE_SETTLING].
     * @return The current drag state
     */
    fun getViewDragState(): Int {
        return mDragState
    }

    /**
     * Enable edge tracking for the selected edges of the parent view.
     * The callback's [Callback.onEdgeTouched] and
     * [Callback.onEdgeDragStarted] methods will only be invoked
     * for edges for which edge tracking has been enabled.
     *
     * @param edgeFlags Combination of edge flags describing the edges to watch
     * @see .EDGE_LEFT
     *
     * @see .EDGE_TOP
     *
     * @see .EDGE_RIGHT
     *
     * @see .EDGE_BOTTOM
     */
    fun setEdgeTrackingEnabled(edgeFlags: Int) {
        mTrackingEdges = edgeFlags
    }

    /**
     * Return the size of an edge. This is the range in pixels along the edges of this view
     * that will actively detect edge touches or drags if edge tracking is enabled.
     *
     * @return The size of an edge in pixels
     * @see .setEdgeTrackingEnabled
     */
    fun getEdgeSize(): Int {
        return mEdgeSize
    }

    /**
     * Capture a specific child view for dragging within the parent. The callback will be notified
     * but [Callback.tryCaptureView] will not be asked permission to
     * capture this view.
     *
     * @param childView Child view to capture
     * @param activePointerId ID of the pointer that is dragging the captured child view
     */
    fun captureChildView(childView: View, activePointerId: Int) {
        if (childView.parent !== mParentView) {
            throw IllegalArgumentException("captureChildView: parameter must be a descendant " +
                    "of the ViewDragHelper's tracked parent view (" + mParentView + ")")
        }

        mCapturedView = childView
        mActivePointerId = activePointerId
        mCallback!!.onViewCaptured(childView, activePointerId)
        setDragState(STATE_DRAGGING)
    }

    /**
     * @return The currently captured view, or null if no view has been captured.
     */
    fun getCapturedView(): View {
        return mCapturedView!!
    }

    /**
     * @return The ID of the pointer currently dragging the captured view,
     * or [.INVALID_POINTER].
     */
    fun getActivePointerId(): Int {
        return mActivePointerId
    }

    /**
     * @return The minimum distance in pixels that the user must travel to initiate a drag
     */
    fun getTouchSlop(): Int {
        return mTouchSlop
    }

    /**
     * The result of a call to this method is equivalent to
     * [.processTouchEvent] receiving an ACTION_CANCEL event.
     */
    fun cancel() {
        mActivePointerId = INVALID_POINTER
        clearMotionHistory()

        mVelocityTracker?.let {
            it.recycle()
            mVelocityTracker = null
        }
    }

    /**
     * [.cancel], but also abort all motion in progress and snap to the end of any
     * animation.
     */
    fun abort() {
        cancel()
        if (mDragState == STATE_SETTLING) {
            val mScroller = mScroller!!
            val oldX = mScroller.currX
            val oldY = mScroller.currY
            mScroller.abortAnimation()
            val newX = mScroller.currX
            val newY = mScroller.currY
            mCallback!!.onViewPositionChanged(mCapturedView!!, newX, newY, newX - oldX, newY - oldY)
        }
        setDragState(STATE_IDLE)
    }


    /**
     * Animate the view `child` to the given (left, top) position.
     * If this method returns true, the caller should invoke [.continueSettling]
     * on each subsequent frame to continue the motion until it returns false. If this method
     * returns false there is no further work to do to complete the movement.
     *
     *
     * This operation does not count as a capture event, though [.getCapturedView]
     * will still report the sliding view while the slide is in progress.
     *
     * @param child Child view to capture and animate
     * @param finalLeft Final left position of child
     * @param finalTop Final top position of child
     * @return true if animation should continue through [.continueSettling] calls
     */
    fun smoothSlideViewTo(child: View, finalLeft: Int, finalTop: Int): Boolean {
        mCapturedView = child
        mActivePointerId = INVALID_POINTER

        return forceSettleCapturedViewAt(finalLeft, finalTop, 0, 0)
    }

    /**
     * Settle the captured view at the given (left, top) position.
     * The appropriate velocity from prior motion will be taken into account.
     * If this method returns true, the caller should invoke [.continueSettling]
     * on each subsequent frame to continue the motion until it returns false. If this method
     * returns false there is no further work to do to complete the movement.
     *
     * @param finalLeft Settled left edge position for the captured view
     * @param finalTop Settled top edge position for the captured view
     * @return true if animation should continue through [.continueSettling] calls
     */
    fun settleCapturedViewAt(finalLeft: Int, finalTop: Int): Boolean {
        if (!mReleaseInProgress) {
            throw IllegalStateException("Cannot settleCapturedViewAt outside of a call to " + "Callback#onViewReleased")
        }

        return forceSettleCapturedViewAt(finalLeft, finalTop,
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId).toInt(),
                VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId).toInt())
    }

    /**
     * Settle the captured view at the given (left, top) position.
     *
     * @param finalLeft Target left position for the captured view
     * @param finalTop Target top position for the captured view
     * @param xvel Horizontal velocity
     * @param yvel Vertical velocity
     * @return true if animation should continue through [.continueSettling] calls
     */
    private fun forceSettleCapturedViewAt(finalLeft: Int, finalTop: Int, xvel: Int, yvel: Int): Boolean {
        val startLeft = mCapturedView!!.left
        val startTop = mCapturedView!!.top
        val dx = finalLeft - startLeft
        val dy = finalTop - startTop

        if (dx == 0 && dy == 0) {
            // Nothing to do. Send callbacks, be done.
            mScroller!!.abortAnimation()
            setDragState(STATE_IDLE)
            return false
        }

        val duration = computeSettleDuration(mCapturedView!!, dx, dy, xvel, yvel)
        mScroller!!.startScroll(startLeft, startTop, dx, dy, duration)

        setDragState(STATE_SETTLING)
        return true
    }

    private fun computeSettleDuration(child: View, dx: Int, dy: Int, xvel: Int, yvel: Int): Int {
        var tmpXVel = xvel
        var tmpYVel = yvel
        tmpXVel = clampMag(tmpXVel, mMinVelocity.toInt(), mMaxVelocity.toInt())
        tmpYVel = clampMag(tmpYVel, mMinVelocity.toInt(), mMaxVelocity.toInt())
        val absDx = Math.abs(dx)
        val absDy = Math.abs(dy)
        val absXVel = Math.abs(tmpXVel)
        val absYVel = Math.abs(tmpYVel)
        val addedVel = absXVel + absYVel
        val addedDistance = absDx + absDy

        val xweight = if (tmpXVel != 0)
            absXVel.toFloat() / addedVel
        else
            absDx.toFloat() / addedDistance
        val yweight = if (tmpYVel != 0)
            absYVel.toFloat() / addedVel
        else
            absDy.toFloat() / addedDistance

        val xduration = computeAxisDuration(dx, tmpXVel, mCallback!!.getViewHorizontalDragRange(child))
        val yduration = computeAxisDuration(dy, tmpYVel, mCallback!!.getViewVerticalDragRange(child))

        return (xduration * xweight + yduration * yweight).toInt()
    }

    private fun computeAxisDuration(delta: Int, velocity: Int, motionRange: Int): Int {
        var tmpVelocity = velocity
        if (delta == 0) {
            return 0
        }

        val width = mParentView!!.width
        val halfWidth = width / 2
        val distanceRatio = Math.min(1f, Math.abs(delta).toFloat() / width)
        val distance = halfWidth + halfWidth * distanceInfluenceForSnapDuration(distanceRatio)

        val duration: Int
        tmpVelocity = Math.abs(tmpVelocity)
        duration = if (tmpVelocity > 0) {
            4 * Math.round(1000 * Math.abs(distance / tmpVelocity))
        } else {
            val range = Math.abs(delta).toFloat() / motionRange
            ((range + 1) * BASE_SETTLE_DURATION).toInt()
        }
        return Math.min(duration, MAX_SETTLE_DURATION)
    }

    /**
     * Clamp the magnitude of value for absMin and absMax.
     * If the value is below the minimum, it will be clamped to zero.
     * If the value is above the maximum, it will be clamped to the maximum.
     *
     * @param value Value to clamp
     * @param absMin Absolute value of the minimum significant value to return
     * @param absMax Absolute value of the maximum value to return
     * @return The clamped value with the same sign as `value`
     */
    private fun clampMag(value: Int, absMin: Int, absMax: Int): Int {
        val absValue = Math.abs(value)
        if (absValue < absMin) return 0
        return if (absValue > absMax) if (value > 0) absMax else -absMax else value
    }

    /**
     * Clamp the magnitude of value for absMin and absMax.
     * If the value is below the minimum, it will be clamped to zero.
     * If the value is above the maximum, it will be clamped to the maximum.
     *
     * @param value Value to clamp
     * @param absMin Absolute value of the minimum significant value to return
     * @param absMax Absolute value of the maximum value to return
     * @return The clamped value with the same sign as `value`
     */
    private fun clampMag(value: Float, absMin: Float, absMax: Float): Float {
        val absValue = Math.abs(value)
        if (absValue < absMin) return 0f
        return if (absValue > absMax) if (value > 0) absMax else -absMax else value
    }

    private fun distanceInfluenceForSnapDuration(f: Float): Float {
        val tmp = (f - 0.5) * (0.3 * Math.PI / 2.0)
        return Math.sin(tmp).toFloat()
    }

    /**
     * Settle the captured view based on standard free-moving fling behavior.
     * The caller should invoke [.continueSettling] on each subsequent frame
     * to continue the motion until it returns false.
     *
     * @param minLeft Minimum X position for the view's left edge
     * @param minTop Minimum Y position for the view's top edge
     * @param maxLeft Maximum X position for the view's left edge
     * @param maxTop Maximum Y position for the view's top edge
     */
    fun flingCapturedView(minLeft: Int, minTop: Int, maxLeft: Int, maxTop: Int) {
        if (!mReleaseInProgress) {
            throw IllegalStateException("Cannot flingCapturedView outside of a call to " + "Callback#onViewReleased")
        }

        mScroller!!.fling(mCapturedView!!.left, mCapturedView!!.top,
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId).toInt(),
                VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId).toInt(),
                minLeft, maxLeft, minTop, maxTop)

        setDragState(STATE_SETTLING)
    }

    /**
     * Move the captured settling view by the appropriate amount for the current time.
     * If `continueSettling` returns true, the caller should call it again
     * on the next frame to continue.
     *
     * @param deferCallbacks true if state callbacks should be deferred via posted message.
     * Set this to true if you are calling this method from
     * [android.view.View.computeScroll] or similar methods
     * invoked as part of layout or drawing.
     * @return true if settle is still in progress
     */
    fun continueSettling(deferCallbacks: Boolean): Boolean {
        // Make sure, there is a captured view
        if (mCapturedView == null) {
            return false
        }
        if (mDragState == STATE_SETTLING) {
            var keepGoing = mScroller!!.computeScrollOffset()
            val x = mScroller!!.currX
            val y = mScroller!!.currY
            val dx = x - mCapturedView!!.left
            val dy = y - mCapturedView!!.top

            if (!keepGoing && dy != 0) { //fix #525
                //Invalid drag state
                mCapturedView!!.top = 0
                return true
            }

            if (dx != 0) {
                mCapturedView!!.offsetLeftAndRight(dx)
            }
            if (dy != 0) {
                mCapturedView!!.offsetTopAndBottom(dy)
            }

            if (dx != 0 || dy != 0) {
                mCallback!!.onViewPositionChanged(mCapturedView!!, x, y, dx, dy)
            }

            if (keepGoing && x == mScroller!!.finalX && y == mScroller!!.finalY) {
                // Close enough. The interpolator/scroller might think we're still moving
                // but the user sure doesn't.
                mScroller!!.abortAnimation()
                keepGoing = mScroller!!.isFinished
            }

            if (!keepGoing) {
                if (deferCallbacks) {
                    mParentView!!.post(mSetIdleRunnable)
                } else {
                    setDragState(STATE_IDLE)
                }
            }
        }

        return mDragState == STATE_SETTLING
    }

    /**
     * Like all callback events this must happen on the UI thread, but release
     * involves some extra semantics. During a release (mReleaseInProgress)
     * is the only time it is valid to call [.settleCapturedViewAt]
     * or [.flingCapturedView].
     */
    private fun dispatchViewReleased(xvel: Float, yvel: Float) {
        mReleaseInProgress = true
        mCallback!!.onViewReleased(mCapturedView!!, xvel, yvel)
        mReleaseInProgress = false

        if (mDragState == STATE_DRAGGING) {
            // onViewReleased didn't call a method that would have changed this. Go idle.
            setDragState(STATE_IDLE)
        }
    }

    private fun clearMotionHistory() {
        if (mInitialMotionX == null) {
            return
        }
        Arrays.fill(mInitialMotionX, 0f)
        Arrays.fill(mInitialMotionY, 0f)
        Arrays.fill(mLastMotionX, 0f)
        Arrays.fill(mLastMotionY, 0f)
        Arrays.fill(mInitialEdgesTouched, 0)
        Arrays.fill(mEdgeDragsInProgress, 0)
        Arrays.fill(mEdgeDragsLocked, 0)
        mPointersDown = 0
    }

    private fun clearMotionHistory(pointerId: Int) {
        if (mInitialMotionX == null || mInitialMotionX!!.size <= pointerId) {
            return
        }
        mInitialMotionX!![pointerId] = 0f
        mInitialMotionY!![pointerId] = 0f
        mLastMotionX!![pointerId] = 0f
        mLastMotionY!![pointerId] = 0f
        mInitialEdgesTouched!![pointerId] = 0
        mEdgeDragsInProgress!![pointerId] = 0
        mEdgeDragsLocked!![pointerId] = 0
        mPointersDown = mPointersDown and (1 shl pointerId).inv()
    }

    private fun ensureMotionHistorySizeForId(pointerId: Int) {
        if (mInitialMotionX == null || mInitialMotionX!!.size <= pointerId) {
            val imx = FloatArray(pointerId + 1)
            val imy = FloatArray(pointerId + 1)
            val lmx = FloatArray(pointerId + 1)
            val lmy = FloatArray(pointerId + 1)
            val iit = IntArray(pointerId + 1)
            val edip = IntArray(pointerId + 1)
            val edl = IntArray(pointerId + 1)

            if (mInitialMotionX != null) {
                @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                run {
                    System.arraycopy(mInitialMotionX, 0, imx, 0, mInitialMotionX!!.size)
                    System.arraycopy(mInitialMotionY, 0, imy, 0, mInitialMotionY!!.size)
                    System.arraycopy(mLastMotionX, 0, lmx, 0, mLastMotionX!!.size)
                    System.arraycopy(mLastMotionY, 0, lmy, 0, mLastMotionY!!.size)
                    System.arraycopy(mInitialEdgesTouched, 0, iit, 0, mInitialEdgesTouched!!.size)
                    System.arraycopy(mEdgeDragsInProgress, 0, edip, 0, mEdgeDragsInProgress!!.size)
                    System.arraycopy(mEdgeDragsLocked, 0, edl, 0, mEdgeDragsLocked!!.size)
                }
            }

            mInitialMotionX = imx
            mInitialMotionY = imy
            mLastMotionX = lmx
            mLastMotionY = lmy
            mInitialEdgesTouched = iit
            mEdgeDragsInProgress = edip
            mEdgeDragsLocked = edl
        }
    }

    private fun saveInitialMotion(x: Float, y: Float, pointerId: Int) {
        ensureMotionHistorySizeForId(pointerId)
        mLastMotionX!![pointerId] = x
        mInitialMotionX!![pointerId] = mLastMotionX!![pointerId]
        mLastMotionY!![pointerId] = y
        mInitialMotionY!![pointerId] = mLastMotionY!![pointerId]
        mInitialEdgesTouched!![pointerId] = getEdgesTouched(x.toInt(), y.toInt())
        mPointersDown = mPointersDown or (1 shl pointerId)
    }

    private fun saveLastMotion(ev: MotionEvent) {
        val pointerCount = MotionEventCompat.getPointerCount(ev)
        for (i in 0 until pointerCount) {
            val pointerId = MotionEventCompat.getPointerId(ev, i)
            val x = MotionEventCompat.getX(ev, i)
            val y = MotionEventCompat.getY(ev, i)
            // Sometimes we can try and save last motion for a pointer never recorded in initial motion. In this case we just discard it.
            if (mLastMotionX != null && mLastMotionY != null
                    && mLastMotionX!!.size > pointerId && mLastMotionY!!.size > pointerId) {
                mLastMotionX!![pointerId] = x
                mLastMotionY!![pointerId] = y
            }
        }
    }

    /**
     * Check if the given pointer ID represents a pointer that is currently down (to the best
     * of the ViewDragHelper's knowledge).
     *
     *
     * The state used to report this information is populated by the methods
     * [.shouldInterceptTouchEvent] or
     * [.processTouchEvent]. If one of these methods has not
     * been called for all relevant MotionEvents to track, the information reported
     * by this method may be stale or incorrect.
     *
     * @param pointerId pointer ID to check; corresponds to IDs provided by MotionEvent
     * @return true if the pointer with the given ID is still down
     */
    fun isPointerDown(pointerId: Int): Boolean {
        return mPointersDown and (1 shl pointerId) != 0
    }

    fun setDragState(state: Int) {
        if (mDragState != state) {
            mDragState = state
            mCallback!!.onViewDragStateChanged(state)
            if (mDragState == STATE_IDLE) {
                mCapturedView = null
            }
        }
    }

    /**
     * Attempt to capture the view with the given pointer ID. The callback will be involved.
     * This will put us into the "dragging" state. If we've already captured this view with
     * this pointer this method will immediately return true without consulting the callback.
     *
     * @param toCapture View to capture
     * @param pointerId Pointer to capture with
     * @return true if capture was successful
     */
    fun tryCaptureViewForDrag(toCapture: View?, pointerId: Int): Boolean {
        if (toCapture === mCapturedView && mActivePointerId == pointerId) {
            // Already done!
            return true
        }
        if (toCapture != null && mCallback!!.tryCaptureView(toCapture, pointerId)) {
            mActivePointerId = pointerId
            captureChildView(toCapture, pointerId) // todo here
            return true
        }
        return false
    }

    /**
     * Tests scrollability within child views of v given a delta of dx.
     *
     * @param v View to test for horizontal scrollability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     * or just its children (false).
     * @param dx Delta scrolled in pixels along the X axis
     * @param dy Delta scrolled in pixels along the Y axis
     * @param x X coordinate of the active touch point
     * @param y Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected fun canScroll(v: View, checkV: Boolean, dx: Int, dy: Int, x: Int, y: Int): Boolean {
        if (v is ViewGroup) {
            val scrollX = v.getScrollX()
            val scrollY = v.getScrollY()
            val count = v.childCount
            // Count backwards - let topmost views consume scroll distance first.
            for (i in count - 1 downTo 0) {
                // TODO: Add versioned support here for transformed views.
                // This will not work for transformed views in Honeycomb+
                val child = v.getChildAt(i)
                if (x + scrollX >= child.left && x + scrollX < child.right &&
                        y + scrollY >= child.top && y + scrollY < child.bottom &&
                        canScroll(child, true, dx, dy, x + scrollX - child.left,
                                y + scrollY - child.top)) {
                    return true
                }
            }
        }

        return checkV && (ViewCompat.canScrollHorizontally(v, -dx) || ViewCompat.canScrollVertically(v, -dy))
    }


    /**
     * Check if this event as provided to the parent view's onInterceptTouchEvent should
     * cause the parent to intercept the touch event stream.
     *
     * @param ev MotionEvent provided to onInterceptTouchEvent
     * @return true if the parent view should return true from onInterceptTouchEvent
     */
    fun shouldInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)
        val actionIndex = MotionEventCompat.getActionIndex(ev)

        if (action == MotionEvent.ACTION_DOWN) {
            // Reset things for a new event stream, just in case we didn't get
            // the whole previous stream.
            cancel()
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                val pointerId = MotionEventCompat.getPointerId(ev, 0)
                saveInitialMotion(x, y, pointerId)

                val toCapture = findTopChildUnder(x.toInt(), y.toInt())

                // Catch a settling view if possible.
                if (toCapture === mCapturedView && mDragState == STATE_SETTLING) {
                    tryCaptureViewForDrag(toCapture, pointerId)
                }

                val edgesTouched = mInitialEdgesTouched!![pointerId]
                if (edgesTouched and mTrackingEdges != 0) {
                    mCallback!!.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                }
            }

            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val pointerId = MotionEventCompat.getPointerId(ev, actionIndex)
                val x = MotionEventCompat.getX(ev, actionIndex)
                val y = MotionEventCompat.getY(ev, actionIndex)

                saveInitialMotion(x, y, pointerId)

                // A ViewDragHelper can only manipulate one view at a time.
                if (mDragState == STATE_IDLE) {
                    val edgesTouched = mInitialEdgesTouched!![pointerId]
                    if (edgesTouched and mTrackingEdges != 0) {
                        mCallback!!.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                    }
                } else if (mDragState == STATE_SETTLING) {
                    // Catch a settling view if possible.
                    val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                    if (toCapture === mCapturedView) {
                        tryCaptureViewForDrag(toCapture, pointerId)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                // First to cross a touch slop over a draggable view wins. Also report edge drags.
                val pointerCount = MotionEventCompat.getPointerCount(ev)
                var i = 0
                while (i < pointerCount && mInitialMotionX != null && mInitialMotionY != null) {
                    val pointerId = MotionEventCompat.getPointerId(ev, i)
                    if (pointerId >= mInitialMotionX!!.size || pointerId >= mInitialMotionY!!.size) {
                        i++
                        continue
                    }
                    val x = MotionEventCompat.getX(ev, i)
                    val y = MotionEventCompat.getY(ev, i)
                    val dx = x - mInitialMotionX!![pointerId]
                    val dy = y - mInitialMotionY!![pointerId]

                    reportNewEdgeDrags(dx, dy, pointerId)
                    if (mDragState == STATE_DRAGGING) {
                        // Callback might have started an edge drag
                        break
                    }

                    /*// todo tmp {
                    val interceptedByScrollView = mParentView?.let { parentView ->
                        var result = false
                        LViews.forEachChildOfType<ScrollView>(parentView) {
                            if (it.isFocused) result = true
                        }
                        result
                    }

                    LLog.debug(javaClass, "interceptedByScrollView = $interceptedByScrollView") // todo solve issue
                    val toCapture = findTopChildUnder(mInitialMotionX!![pointerId].toInt(), mInitialMotionY!![pointerId].toInt())
                    if (toCapture != null && checkTouchSlop(toCapture, dx, dy)) {
                        if (interceptedByScrollView == true) break
                        if (tryCaptureViewForDrag(toCapture, pointerId)) break
                    }
                    // todo tmp }*/

                    val toCapture = findTopChildUnder(mInitialMotionX!![pointerId].toInt(), mInitialMotionY!![pointerId].toInt())
                    if (toCapture != null && checkTouchSlop(toCapture, dx, dy) && tryCaptureViewForDrag(toCapture, pointerId)) { // todo here
                        break
                    }

                    i++
                }
                saveLastMotion(ev)
            }

            MotionEventCompat.ACTION_POINTER_UP -> {
                val pointerId = MotionEventCompat.getPointerId(ev, actionIndex)
                clearMotionHistory(pointerId)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                cancel()
            }
        }

        //LLog.debug(javaClass, "$mDragState (${mDragState == STATE_DRAGGING})") // todo solve issue
        return mDragState == STATE_DRAGGING
    }


    /**
     * Process a touch event received by the parent view. This method will dispatch callback events
     * as needed before returning. The parent view's onTouchEvent implementation should call this.
     *
     * @param ev The touch event received by the parent view
     */
    fun processTouchEvent(ev: MotionEvent) {
        val action = MotionEventCompat.getActionMasked(ev)
        val actionIndex = MotionEventCompat.getActionIndex(ev)

        if (action == MotionEvent.ACTION_DOWN) {
            // Reset things for a new event stream, just in case we didn't get
            // the whole previous stream.
            cancel()
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x = ev.x
                val y = ev.y
                val pointerId = MotionEventCompat.getPointerId(ev, 0)
                val toCapture = findTopChildUnder(x.toInt(), y.toInt())

                saveInitialMotion(x, y, pointerId)

                // Since the parent is already directly processing this touch event,
                // there is no reason to delay for a slop before dragging.
                // Start immediately if possible.
                tryCaptureViewForDrag(toCapture, pointerId)

                val edgesTouched = mInitialEdgesTouched!![pointerId]
                if (edgesTouched and mTrackingEdges != 0) {
                    mCallback!!.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                }
            }

            MotionEventCompat.ACTION_POINTER_DOWN -> {
                val pointerId = MotionEventCompat.getPointerId(ev, actionIndex)
                val x = MotionEventCompat.getX(ev, actionIndex)
                val y = MotionEventCompat.getY(ev, actionIndex)

                saveInitialMotion(x, y, pointerId)

                // A ViewDragHelper can only manipulate one view at a time.
                if (mDragState == STATE_IDLE) {
                    // If we're idle we can do anything! Treat it like a normal down event.

                    val toCapture = findTopChildUnder(x.toInt(), y.toInt())
                    tryCaptureViewForDrag(toCapture, pointerId)

                    val edgesTouched = mInitialEdgesTouched!![pointerId]
                    if (edgesTouched and mTrackingEdges != 0) {
                        mCallback!!.onEdgeTouched(edgesTouched and mTrackingEdges, pointerId)
                    }
                } else if (isCapturedViewUnder(x.toInt(), y.toInt())) {
                    // We're still tracking a captured view. If the same view is under this
                    // point, we'll swap to controlling it with this pointer instead.
                    // (This will still work if we're "catching" a settling view.)

                    tryCaptureViewForDrag(mCapturedView, pointerId)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (mDragState == STATE_DRAGGING) {
                    val index = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                    val x = MotionEventCompat.getX(ev, index)
                    val y = MotionEventCompat.getY(ev, index)
                    val idx = (x - mLastMotionX!![mActivePointerId]).toInt()
                    val idy = (y - mLastMotionY!![mActivePointerId]).toInt()

                    dragTo(mCapturedView!!.left + idx, mCapturedView!!.top + idy, idx, idy)

                    saveLastMotion(ev)
                } else {
                    // Check to see if any pointer is now over a draggable view.
                    val pointerCount = MotionEventCompat.getPointerCount(ev)
                    for (i in 0 until pointerCount) {
                        val pointerId = MotionEventCompat.getPointerId(ev, i)
                        val x = MotionEventCompat.getX(ev, i)
                        val y = MotionEventCompat.getY(ev, i)
                        val dx = x - mInitialMotionX!![pointerId]
                        val dy = y - mInitialMotionY!![pointerId]

                        reportNewEdgeDrags(dx, dy, pointerId)
                        if (mDragState == STATE_DRAGGING) {
                            // Callback might have started an edge drag.
                            break
                        }

                        val toCapture = findTopChildUnder(mInitialMotionX!![pointerId].toInt(), mInitialMotionY!![pointerId].toInt())
                        if (checkTouchSlop(toCapture, dx, dy) && tryCaptureViewForDrag(toCapture, pointerId)) {
                            break
                        }
                    }
                    saveLastMotion(ev)
                }
            }

            MotionEventCompat.ACTION_POINTER_UP -> {
                val pointerId = MotionEventCompat.getPointerId(ev, actionIndex)
                if (mDragState == STATE_DRAGGING && pointerId == mActivePointerId) {
                    // Try to find another pointer that's still holding on to the captured view.
                    var newActivePointer = INVALID_POINTER
                    val pointerCount = MotionEventCompat.getPointerCount(ev)
                    for (i in 0 until pointerCount) {
                        val id = MotionEventCompat.getPointerId(ev, i)
                        if (id == mActivePointerId) {
                            // This one's going away, skip.
                            continue
                        }

                        val x = MotionEventCompat.getX(ev, i)
                        val y = MotionEventCompat.getY(ev, i)
                        if (findTopChildUnder(x.toInt(), y.toInt()) === mCapturedView && tryCaptureViewForDrag(mCapturedView, id)) {
                            newActivePointer = mActivePointerId
                            break
                        }
                    }

                    if (newActivePointer == INVALID_POINTER) {
                        // We didn't find another pointer still touching the view, release it.
                        releaseViewForPointerUp()
                    }
                }
                clearMotionHistory(pointerId)
            }

            MotionEvent.ACTION_UP -> {
                if (mDragState == STATE_DRAGGING) {
                    releaseViewForPointerUp()
                }
                cancel()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (mDragState == STATE_DRAGGING) {
                    dispatchViewReleased(0f, 0f)
                }
                cancel()
            }
        }
    }

    private fun reportNewEdgeDrags(dx: Float, dy: Float, pointerId: Int) {
        var dragsStarted = 0
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_LEFT)) {
            dragsStarted = dragsStarted or EDGE_LEFT
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_TOP)) {
            dragsStarted = dragsStarted or EDGE_TOP
        }
        if (checkNewEdgeDrag(dx, dy, pointerId, EDGE_RIGHT)) {
            dragsStarted = dragsStarted or EDGE_RIGHT
        }
        if (checkNewEdgeDrag(dy, dx, pointerId, EDGE_BOTTOM)) {
            dragsStarted = dragsStarted or EDGE_BOTTOM
        }

        if (dragsStarted != 0) {
            mEdgeDragsInProgress!![pointerId] = mEdgeDragsInProgress!![pointerId] or dragsStarted
            mCallback!!.onEdgeDragStarted(dragsStarted, pointerId)
        }
    }

    private fun checkNewEdgeDrag(delta: Float, odelta: Float, pointerId: Int, edge: Int): Boolean {
        val absDelta = Math.abs(delta)
        val absODelta = Math.abs(odelta)

        if (mInitialEdgesTouched!![pointerId] and edge != edge || mTrackingEdges and edge == 0 ||
                mEdgeDragsLocked!![pointerId] and edge == edge ||
                mEdgeDragsInProgress!![pointerId] and edge == edge ||
                absDelta <= mTouchSlop && absODelta <= mTouchSlop) {
            return false
        }
        if (absDelta < absODelta * 0.5f && mCallback!!.onEdgeLock(edge)) {
            mEdgeDragsLocked!![pointerId] = mEdgeDragsLocked!![pointerId] or edge
            return false
        }
        return mEdgeDragsInProgress!![pointerId] and edge == 0 && absDelta > mTouchSlop
    }


    /**
     * Check if we've crossed a reasonable touch slop for the given child view.
     * If the child cannot be dragged along the horizontal or vertical axis, motion
     * along that axis will not count toward the slop check.
     *
     * @param child Child to check
     * @param dx Motion since initial position along X axis
     * @param dy Motion since initial position along Y axis
     * @return true if the touch slop has been crossed
     */
    private fun checkTouchSlop(child: View?, dx: Float, dy: Float): Boolean {
        if (child == null) {
            return false
        }
        val checkHorizontal = mCallback!!.getViewHorizontalDragRange(child) > 0
        val checkVertical = mCallback!!.getViewVerticalDragRange(child) > 0

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop
        } else if (checkHorizontal) {
            return Math.abs(dx) > mTouchSlop
        } else if (checkVertical) {
            return Math.abs(dy) > mTouchSlop
        }
        return false
    }

    /**
     * Check if any pointer tracked in the current gesture has crossed
     * the required slop threshold.
     *
     *
     * This depends on internal state populated by
     * [.shouldInterceptTouchEvent] or
     * [.processTouchEvent]. You should only rely on
     * the results of this method after all currently available touch data
     * has been provided to one of these two methods.
     *
     * @param directions Combination of direction flags, see [.DIRECTION_HORIZONTAL],
     * [.DIRECTION_VERTICAL], [.DIRECTION_ALL]
     * @return true if the slop threshold has been crossed, false otherwise
     */
    fun checkTouchSlop(directions: Int): Boolean {
        val count = mInitialMotionX!!.size
        for (i in 0 until count) {
            if (checkTouchSlop(directions, i)) {
                return true
            }
        }
        return false
    }

    /**
     * Check if the specified pointer tracked in the current gesture has crossed
     * the required slop threshold.
     *
     *
     * This depends on internal state populated by
     * [.shouldInterceptTouchEvent] or
     * [.processTouchEvent]. You should only rely on
     * the results of this method after all currently available touch data
     * has been provided to one of these two methods.
     *
     * @param directions Combination of direction flags, see [.DIRECTION_HORIZONTAL],
     * [.DIRECTION_VERTICAL], [.DIRECTION_ALL]
     * @param pointerId ID of the pointer to slop check as specified by MotionEvent
     * @return true if the slop threshold has been crossed, false otherwise
     */
    fun checkTouchSlop(directions: Int, pointerId: Int): Boolean {
        if (!isPointerDown(pointerId)) {
            return false
        }

        val checkHorizontal = directions and DIRECTION_HORIZONTAL == DIRECTION_HORIZONTAL
        val checkVertical = directions and DIRECTION_VERTICAL == DIRECTION_VERTICAL

        val dx = mLastMotionX!![pointerId] - mInitialMotionX!![pointerId]
        val dy = mLastMotionY!![pointerId] - mInitialMotionY!![pointerId]

        if (checkHorizontal && checkVertical) {
            return dx * dx + dy * dy > mTouchSlop * mTouchSlop
        } else if (checkHorizontal) {
            return Math.abs(dx) > mTouchSlop
        } else if (checkVertical) {
            return Math.abs(dy) > mTouchSlop
        }
        return false
    }

    /**
     * Check if any of the edges specified were initially touched in the currently active gesture.
     * If there is no currently active gesture this method will return false.
     *
     * @param edges Edges to check for an initial edge touch. See [.EDGE_LEFT],
     * [.EDGE_TOP], [.EDGE_RIGHT], [.EDGE_BOTTOM] and
     * [.EDGE_ALL]
     * @return true if any of the edges specified were initially touched in the current gesture
     */
    fun isEdgeTouched(edges: Int): Boolean {
        val count = mInitialEdgesTouched!!.size
        for (i in 0 until count) {
            if (isEdgeTouched(edges, i)) {
                return true
            }
        }
        return false
    }

    /**
     * Check if any of the edges specified were initially touched by the pointer with
     * the specified ID. If there is no currently active gesture or if there is no pointer with
     * the given ID currently down this method will return false.
     *
     * @param edges Edges to check for an initial edge touch. See [.EDGE_LEFT],
     * [.EDGE_TOP], [.EDGE_RIGHT], [.EDGE_BOTTOM] and
     * [.EDGE_ALL]
     * @return true if any of the edges specified were initially touched in the current gesture
     */
    fun isEdgeTouched(edges: Int, pointerId: Int): Boolean {
        return isPointerDown(pointerId) && mInitialEdgesTouched!![pointerId] and edges != 0
    }

    fun isDragging(): Boolean {
        return mDragState == STATE_DRAGGING
    }

    private fun releaseViewForPointerUp() {
        mVelocityTracker!!.computeCurrentVelocity(1000, mMaxVelocity)
        val xvel = clampMag(
                VelocityTrackerCompat.getXVelocity(mVelocityTracker, mActivePointerId),
                mMinVelocity, mMaxVelocity)
        val yvel = clampMag(
                VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId),
                mMinVelocity, mMaxVelocity)
        dispatchViewReleased(xvel, yvel)
    }

    private fun dragTo(left: Int, top: Int, dx: Int, dy: Int) {
        var clampedX = left
        var clampedY = top
        val oldLeft = mCapturedView!!.left
        val oldTop = mCapturedView!!.top
        if (dx != 0) {
            clampedX = mCallback!!.clampViewPositionHorizontal(mCapturedView!!, left, dx)
            mCapturedView!!.offsetLeftAndRight(clampedX - oldLeft)
        }
        if (dy != 0) {
            clampedY = mCallback!!.clampViewPositionVertical(mCapturedView!!, top, dy)
            mCapturedView!!.offsetTopAndBottom(clampedY - oldTop)
        }

        if (dx != 0 || dy != 0) {
            val clampedDx = clampedX - oldLeft
            val clampedDy = clampedY - oldTop
            mCallback!!.onViewPositionChanged(mCapturedView!!, clampedX, clampedY,
                    clampedDx, clampedDy)
        }
    }

    /**
     * Determine if the currently captured view is under the given point in the
     * parent view's coordinate system. If there is no captured view this method
     * will return false.
     *
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return true if the captured view is under the given point, false otherwise
     */
    fun isCapturedViewUnder(x: Int, y: Int): Boolean {
        return isViewUnder(mCapturedView, x, y)
    }

    /**
     * Determine if the supplied view is under the given point in the
     * parent view's coordinate system.
     *
     * @param view Child view of the parent to hit test
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return true if the supplied view is under the given point, false otherwise
     */
    fun isViewUnder(view: View?, x: Int, y: Int): Boolean {
        return if (view == null) {
            false
        } else x >= view.left &&
                x < view.right &&
                y >= view.top &&
                y < view.bottom
    }

    /**
     * Find the topmost child under the given point within the parent view's coordinate system.
     * The child order is determined using [Callback.getOrderedChildIndex].
     *
     * @param x X position to test in the parent's coordinate system
     * @param y Y position to test in the parent's coordinate system
     * @return The topmost child view under (x, y) or null if none found.
     */
    fun findTopChildUnder(x: Int, y: Int): View? {
        val childCount = mParentView!!.childCount
        for (i in childCount - 1 downTo 0) {
            val child = mParentView!!.getChildAt(mCallback!!.getOrderedChildIndex(i))
            if (x >= child.left && x < child.right &&
                    y >= child.top && y < child.bottom) {
                return child
            }
        }
        return null
    }

    private fun getEdgesTouched(x: Int, y: Int): Int {
        var result = 0

        if (x < mParentView!!.left + mEdgeSize) result = result or EDGE_LEFT
        if (y < mParentView!!.top + mEdgeSize) result = result or EDGE_TOP
        if (x > mParentView!!.right - mEdgeSize) result = result or EDGE_RIGHT
        if (y > mParentView!!.bottom - mEdgeSize) result = result or EDGE_BOTTOM

        return result
    }


    //
    // Companion

    companion object {

        /**
         * A null/invalid pointer ID.
         */
        const val INVALID_POINTER = -1

        /**
         * A view is not currently being dragged or animating as a result of a fling/snap.
         */
        const val STATE_IDLE = 0

        /**
         * A view is currently being dragged. The position is currently changing as a result
         * of user input or simulated user input.
         */
        const val STATE_DRAGGING = 1

        /**
         * A view is currently settling into place as a result of a fling or
         * predefined non-interactive motion.
         */
        const val STATE_SETTLING = 2

        /**
         * Edge flag indicating that the left edge should be affected.
         */
        const val EDGE_LEFT = 1 shl 0

        /**
         * Edge flag indicating that the right edge should be affected.
         */
        const val EDGE_RIGHT = 1 shl 1

        /**
         * Edge flag indicating that the top edge should be affected.
         */
        const val EDGE_TOP = 1 shl 2

        /**
         * Edge flag indicating that the bottom edge should be affected.
         */
        const val EDGE_BOTTOM = 1 shl 3

        /**
         * Edge flag set indicating all edges should be affected.
         */
        const val EDGE_ALL = EDGE_LEFT or EDGE_TOP or EDGE_RIGHT or EDGE_BOTTOM

        /**
         * Indicates that a check should occur along the horizontal axis
         */
        const val DIRECTION_HORIZONTAL = 1 shl 0

        /**
         * Indicates that a check should occur along the vertical axis
         */
        const val DIRECTION_VERTICAL = 1 shl 1

        /**
         * Indicates that a check should occur along all axes
         */
        const val DIRECTION_ALL = DIRECTION_HORIZONTAL or DIRECTION_VERTICAL

        private const val EDGE_SIZE = 20 // dp

        private const val BASE_SETTLE_DURATION = 256 // ms
        private const val MAX_SETTLE_DURATION = 600 // ms

        /**
         * Factory method to create a new ViewDragHelper.
         *
         * @param forParent Parent view to monitor
         * @param cb Callback to provide information and receive events
         * @return a new ViewDragHelper instance
         */
        fun create(forParent: ViewGroup, cb: Callback): ViewDragHelper {
            return ViewDragHelper(forParent.context, forParent, null, cb)
        }

        /**
         * Factory method to create a new ViewDragHelper with the specified interpolator.
         *
         * @param forParent Parent view to monitor
         * @param interpolator interpolator for scroller
         * @param cb Callback to provide information and receive events
         * @return a new ViewDragHelper instance
         */
        fun create(forParent: ViewGroup, interpolator: Interpolator?, cb: Callback): ViewDragHelper {
            return ViewDragHelper(forParent.context, forParent, interpolator, cb)
        }

        /**
         * Factory method to create a new ViewDragHelper.
         *
         * @param forParent Parent view to monitor
         * @param sensitivity Multiplier for how sensitive the helper should be about detecting
         * the start of a drag. Larger values are more sensitive. 1.0f is normal.
         * @param cb Callback to provide information and receive events
         * @return a new ViewDragHelper instance
         */
        fun create(forParent: ViewGroup, sensitivity: Float, cb: Callback): ViewDragHelper {
            val helper = create(forParent, cb)
            helper.mTouchSlop = (helper.mTouchSlop * (1 / sensitivity)).toInt()
            return helper
        }

        /**
         * Factory method to create a new ViewDragHelper with the specified interpolator.
         *
         * @param forParent Parent view to monitor
         * @param sensitivity Multiplier for how sensitive the helper should be about detecting
         * the start of a drag. Larger values are more sensitive. 1.0f is normal.
         * @param interpolator interpolator for scroller
         * @param cb Callback to provide information and receive events
         * @return a new ViewDragHelper instance
         */
        fun create(forParent: ViewGroup, sensitivity: Float, interpolator: Interpolator?, cb: Callback): ViewDragHelper {
            val helper = create(forParent, interpolator, cb)
            helper.mTouchSlop = (helper.mTouchSlop * (1 / sensitivity)).toInt()
            return helper
        }

    }


}