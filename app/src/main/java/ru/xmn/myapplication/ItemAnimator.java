package ru.xmn.myapplication;

import android.support.v4.animation.AnimatorCompatHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class MultiSelectItemAnimator extends SimpleItemAnimator {
    private final List<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<RecyclerView.ViewHolder>();
    private final List<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<RecyclerView.ViewHolder>();
    private final List<MoveInfo> mPendingMoves = new ArrayList<MoveInfo>();
    private final List<ChangeInfo> mPendingChanges = new ArrayList<ChangeInfo>();
    private final List<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<ArrayList<RecyclerView.ViewHolder>>();
    private final List<ArrayList<MoveInfo>> mMovesList = new ArrayList<ArrayList<MoveInfo>>();
    private final List<ArrayList<ChangeInfo>> mChangesList = new ArrayList<ArrayList<ChangeInfo>>();
    private final List<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<RecyclerView.ViewHolder>();
    private final List<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<RecyclerView.ViewHolder>();
    private final List<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<RecyclerView.ViewHolder>();
    private final List<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<RecyclerView.ViewHolder>();
    private static final boolean DEBUG = false;

    public boolean isRunning() {
        return !this.mPendingAdditions.isEmpty() ||
                !this.mPendingChanges.isEmpty() ||
                !this.mPendingMoves.isEmpty() ||
                !this.mPendingRemovals.isEmpty() ||
                !this.mMoveAnimations.isEmpty() ||
                !this.mRemoveAnimations.isEmpty() ||
                !this.mAddAnimations.isEmpty() ||
                !this.mChangeAnimations.isEmpty() ||
                !this.mMovesList.isEmpty() ||
                !this.mAdditionsList.isEmpty() ||
                !this.mChangesList.isEmpty();
    }

    public void runPendingAnimations() {
        boolean removalsPending = !this.mPendingRemovals.isEmpty();
        boolean movesPending = !this.mPendingMoves.isEmpty();
        boolean changesPending = !this.mPendingChanges.isEmpty();
        boolean additionsPending = !this.mPendingAdditions.isEmpty();
        if (removalsPending || movesPending || additionsPending || changesPending) {
            for (RecyclerView.ViewHolder holder :
                    mPendingRemovals) {
                animateRemoveImpl(holder);
            }

            this.mPendingRemovals.clear();
            if (movesPending) {
                additions1 = new ArrayList();
                additions1.addAll((Collection) this.mPendingMoves);
                this.mMovesList.add(additions1);
                this.mPendingMoves.clear();
                adder1 = (Runnable) (new Runnable() {
                    public final void run() {
                        Iterator var2 = additions1.iterator();

                        while (var2.hasNext()) {
                            MultiSelectItemAnimator.MoveInfo moveInfo = (MultiSelectItemAnimator.MoveInfo) var2.next();
                            MultiSelectItemAnimator.this.animateMoveImpl(moveInfo.getHolder(), moveInfo.getFromX(), moveInfo.getFromY(), moveInfo.getToX(), moveInfo.getToY());
                        }

                        additions1.clear();
                        MultiSelectItemAnimator.this.mMovesList.remove(additions1);
                    }
                });
                if (removalsPending) {
                    View removeDuration = ((MultiSelectItemAnimator.MoveInfo) additions1.get(0)).getHolder().itemView;
                    ViewCompat.postOnAnimationDelayed(removeDuration, adder1, this.getRemoveDuration());
                } else {
                    adder1.run();
                }
            }

            if (changesPending) {
                additions1 = new ArrayList();
                additions1.addAll((Collection) this.mPendingChanges);
                this.mChangesList.add(additions1);
                this.mPendingChanges.clear();
                adder1 = (Runnable) (new Runnable() {
                    public final void run() {
                        Iterator var2 = additions1.iterator();

                        while (var2.hasNext()) {
                            MultiSelectItemAnimator.ChangeInfo change = (MultiSelectItemAnimator.ChangeInfo) var2.next();
                            MultiSelectItemAnimator var10000 = MultiSelectItemAnimator.this;
                            Intrinsics.checkExpressionValueIsNotNull(change, "change");
                            var10000.animateChangeImpl(change);
                        }

                        additions1.clear();
                        MultiSelectItemAnimator.this.mChangesList.remove(additions1);
                    }
                });
                if (removalsPending) {
                    RecyclerView.ViewHolder removeDuration1 = ((MultiSelectItemAnimator.ChangeInfo) additions1.get(0)).getOldHolder();
                    if (removeDuration1 == null) {
                        Intrinsics.throwNpe();
                    }

                    ViewCompat.postOnAnimationDelayed(removeDuration1.itemView, adder1, this.getRemoveDuration());
                } else {
                    adder1.run();
                }
            }

            if (additionsPending) {
                additions1 = new ArrayList();
                additions1.addAll((Collection) this.mPendingAdditions);
                this.mAdditionsList.add(additions1);
                this.mPendingAdditions.clear();
                adder1 = (Runnable) (new Runnable() {
                    public final void run() {
                        Iterator var2 = additions1.iterator();

                        while (var2.hasNext()) {
                            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) var2.next();
                            MultiSelectItemAnimator var10000 = MultiSelectItemAnimator.this;
                            Intrinsics.checkExpressionValueIsNotNull(holder, "holder");
                            var10000.animateAddImpl(holder);
                        }

                        additions1.clear();
                        MultiSelectItemAnimator.this.mAdditionsList.remove(additions1);
                    }
                });
                if (!removalsPending && !movesPending && !changesPending) {
                    adder1.run();
                } else {
                    long removeDuration2 = removalsPending ? this.getRemoveDuration() : 0L;
                    long moveDuration = movesPending ? this.getMoveDuration() : 0L;
                    long changeDuration = changesPending ? this.getChangeDuration() : 0L;
                    long totalDelay = removeDuration2 + Math.max(moveDuration, changeDuration);
                    View view = ((RecyclerView.ViewHolder) additions1.get(0)).itemView;
                    ViewCompat.postOnAnimationDelayed(view, adder1, totalDelay);
                }
            }

        }
    }

    public boolean animateRemove(@NotNull RecyclerView.ViewHolder holder) {
        Intrinsics.checkParameterIsNotNull(holder, "holder");
        this.resetAnimation(holder);
        this.mPendingRemovals.add(holder);
        return true;
    }

    private final void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        View view = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        this.mRemoveAnimations.add(holder);
        animation.setDuration(this.getRemoveDuration()).alpha(0.0F).setListener((ViewPropertyAnimatorListener) (new MultiSelectItemAnimator.VpaListenerAdapter() {
            public void onAnimationStart(@NotNull View view) {
                Intrinsics.checkParameterIsNotNull(view, "view");
                MultiSelectItemAnimator.this.dispatchRemoveStarting(holder);
            }

            public void onAnimationEnd(@NotNull View view) {
                Intrinsics.checkParameterIsNotNull(view, "view");
                animation.setListener((ViewPropertyAnimatorListener) null);
                ViewCompat.setAlpha(view, 1.0F);
                MultiSelectItemAnimator.this.dispatchRemoveFinished(holder);
                MultiSelectItemAnimator.this.mRemoveAnimations.remove(holder);
                MultiSelectItemAnimator.this.dispatchFinishedWhenDone();
            }
        })).start();
    }

    public boolean animateAdd(@NotNull RecyclerView.ViewHolder holder) {
        Intrinsics.checkParameterIsNotNull(holder, "holder");
        Log.d(">>>", holder.toString());
        this.resetAnimation(holder);
        ViewCompat.setAlpha(holder.itemView, 0.0F);
        this.mPendingAdditions.add(holder);
        return true;
    }

    private final void animateAddImpl(final RecyclerView.ViewHolder holder) {
        View view = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        this.mAddAnimations.add(holder);
        animation.alpha(1.0F).setDuration(this.getAddDuration()).setListener((ViewPropertyAnimatorListener) (new MultiSelectItemAnimator.VpaListenerAdapter() {
            public void onAnimationStart(@NotNull View view) {
                Intrinsics.checkParameterIsNotNull(view, "view");
                MultiSelectItemAnimator.this.dispatchAddStarting(holder);
            }

            public void onAnimationCancel(@NotNull View view) {
                Intrinsics.checkParameterIsNotNull(view, "view");
                ViewCompat.setAlpha(view, 1.0F);
            }

            public void onAnimationEnd(@NotNull View view) {
                Intrinsics.checkParameterIsNotNull(view, "view");
                if (view.getParent() instanceof RecyclerView) {
                    animation.setListener((ViewPropertyAnimatorListener) null);
                    MultiSelectItemAnimator.this.dispatchAddFinished(holder);
                    MultiSelectItemAnimator.this.mAddAnimations.remove(holder);
                    MultiSelectItemAnimator.this.dispatchFinishedWhenDone();
                }

            }
        })).start();
    }

    public boolean animateMove(@NotNull RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        Intrinsics.checkParameterIsNotNull(holder, "holder");
        View view = holder.itemView;
        int fromX1 = fromX + (int) ViewCompat.getTranslationX(holder.itemView);
        int fromY1 = fromY + (int) ViewCompat.getTranslationY(holder.itemView);
        this.resetAnimation(holder);
        int deltaX = toX - fromX1;
        int deltaY = toY - fromY1;
        if (deltaX == 0 && deltaY == 0) {
            this.dispatchMoveFinished(holder);
            return false;
        } else {
            if (deltaX != 0) {
                ViewCompat.setTranslationX(view, (float) (-deltaX));
            }

            if (deltaY != 0) {
                ViewCompat.setTranslationY(view, (float) (-deltaY));
            }

            Collection var11 = (Collection) this.mPendingMoves;
            MultiSelectItemAnimator.MoveInfo var12 = new MultiSelectItemAnimator.MoveInfo(holder, fromX1, fromY1, toX, toY);
            var11.add(var12);
            return true;
        }
    }

    private final void animateMoveImpl(final RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        View view = holder.itemView;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;
        if (deltaX != 0) {
            ViewCompat.animate(view).translationX(0.0F);
        }

        if (deltaY != 0) {
            ViewCompat.animate(view).translationY(0.0F);
        }

        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
        this.mMoveAnimations.add(holder);
        animation.setDuration(this.getMoveDuration()).setListener((ViewPropertyAnimatorListener) (new MultiSelectItemAnimator.VpaListenerAdapter() {
            public void onAnimationStart(@NotNull View view) {
                Intrinsics.checkParameterIsNotNull(view, "view");
                MultiSelectItemAnimator.this.dispatchMoveStarting(holder);
            }

            public void onAnimationCancel(@NotNull View view) {
                Intrinsics.checkParameterIsNotNull(view, "view");
                if (deltaX != 0) {
                    ViewCompat.setTranslationX(view, 0.0F);
                }

                if (deltaY != 0) {
                    ViewCompat.setTranslationY(view, 0.0F);
                }

            }

            public void onAnimationEnd(@NotNull View view) {
                Intrinsics.checkParameterIsNotNull(view, "view");
                if (view.getParent() instanceof RecyclerView) {
                    animation.setListener((ViewPropertyAnimatorListener) null);
                    MultiSelectItemAnimator.this.dispatchMoveFinished(holder);
                    MultiSelectItemAnimator.this.mMoveAnimations.remove(holder);
                    MultiSelectItemAnimator.this.dispatchFinishedWhenDone();
                }

            }
        })).start();
    }

    public boolean animateChange(@NotNull RecyclerView.ViewHolder oldHolder, @Nullable RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        Intrinsics.checkParameterIsNotNull(oldHolder, "oldHolder");
        if (oldHolder == newHolder) {
            return this.animateMove(oldHolder, fromX, fromY, toX, toY);
        } else {
            float prevTranslationX = ViewCompat.getTranslationX(oldHolder.itemView);
            float prevTranslationY = ViewCompat.getTranslationY(oldHolder.itemView);
            float prevAlpha = ViewCompat.getAlpha(oldHolder.itemView);
            this.resetAnimation(oldHolder);
            int deltaX = (int) ((float) toX - (float) fromX - prevTranslationX);
            int deltaY = (int) ((float) toY - (float) fromY - prevTranslationY);
            ViewCompat.setTranslationX(oldHolder.itemView, prevTranslationX);
            ViewCompat.setTranslationY(oldHolder.itemView, prevTranslationY);
            ViewCompat.setAlpha(oldHolder.itemView, prevAlpha);
            if (newHolder != null) {
                this.resetAnimation(newHolder);
                ViewCompat.setTranslationX(newHolder.itemView, (float) (-deltaX));
                ViewCompat.setTranslationY(newHolder.itemView, (float) (-deltaY));
                ViewCompat.setAlpha(newHolder.itemView, 0.0F);
            }

            Collection var12 = (Collection) this.mPendingChanges;
            MultiSelectItemAnimator.ChangeInfo var13 = new MultiSelectItemAnimator.ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY);
            var12.add(var13);
            return true;
        }
    }

    private final void animateChangeImpl(final MultiSelectItemAnimator.ChangeInfo changeInfo) {
        RecyclerView.ViewHolder holder = changeInfo.getOldHolder();
        View view = holder != null ? holder.itemView : null;
        RecyclerView.ViewHolder newHolder = changeInfo.getNewHolder();
        final View newView = newHolder != null ? newHolder.itemView : null;
        List var10000;
        RecyclerView.ViewHolder var10001;
        final ViewPropertyAnimatorCompat newViewAnimation;
        if (view != null) {
            newViewAnimation = ViewCompat.animate(view).setDuration(this.getChangeDuration());
            var10000 = this.mChangeAnimations;
            var10001 = changeInfo.getOldHolder();
            if (var10001 == null) {
                Intrinsics.throwNpe();
            }

            var10000.add(var10001);
            newViewAnimation.translationX((float) (changeInfo.getToX() - changeInfo.getFromX()));
            newViewAnimation.translationY((float) (changeInfo.getToY() - changeInfo.getFromY()));
            newViewAnimation.alpha(0.0F).setListener((ViewPropertyAnimatorListener) (new MultiSelectItemAnimator.VpaListenerAdapter() {
                public void onAnimationStart(@NotNull View view) {
                    Intrinsics.checkParameterIsNotNull(view, "view");
                    MultiSelectItemAnimator.this.dispatchChangeStarting(changeInfo.getOldHolder(), true);
                }

                public void onAnimationEnd(@NotNull View view) {
                    Intrinsics.checkParameterIsNotNull(view, "view");
                    newViewAnimation.setListener((ViewPropertyAnimatorListener) null);
                    ViewCompat.setAlpha(view, 1.0F);
                    ViewCompat.setTranslationX(view, 0.0F);
                    ViewCompat.setTranslationY(view, 0.0F);
                    MultiSelectItemAnimator.this.dispatchChangeFinished(changeInfo.getOldHolder(), true);
                    List var10000 = MultiSelectItemAnimator.this.mChangeAnimations;
                    RecyclerView.ViewHolder var10001 = changeInfo.getOldHolder();
                    if (var10001 == null) {
                        Intrinsics.throwNpe();
                    }

                    var10000.remove(var10001);
                    MultiSelectItemAnimator.this.dispatchFinishedWhenDone();
                }
            })).start();
        }

        if (newView != null) {
            newViewAnimation = ViewCompat.animate(newView);
            var10000 = this.mChangeAnimations;
            var10001 = changeInfo.getNewHolder();
            if (var10001 == null) {
                Intrinsics.throwNpe();
            }

            var10000.add(var10001);
            newViewAnimation.translationX(0.0F).translationY(0.0F).setDuration(this.getChangeDuration()).alpha(1.0F).setListener((ViewPropertyAnimatorListener) (new MultiSelectItemAnimator.VpaListenerAdapter() {
                public void onAnimationStart(@NotNull View view) {
                    Intrinsics.checkParameterIsNotNull(view, "view");
                    MultiSelectItemAnimator.this.dispatchChangeStarting(changeInfo.getNewHolder(), false);
                }

                public void onAnimationEnd(@NotNull View view) {
                    Intrinsics.checkParameterIsNotNull(view, "view");
                    newViewAnimation.setListener((ViewPropertyAnimatorListener) null);
                    ViewCompat.setAlpha(newView, 1.0F);
                    ViewCompat.setTranslationX(newView, 0.0F);
                    ViewCompat.setTranslationY(newView, 0.0F);
                    MultiSelectItemAnimator.this.dispatchChangeFinished(changeInfo.getNewHolder(), false);
                    List var10000 = MultiSelectItemAnimator.this.mChangeAnimations;
                    RecyclerView.ViewHolder var10001 = changeInfo.getNewHolder();
                    if (var10001 == null) {
                        Intrinsics.throwNpe();
                    }

                    var10000.remove(var10001);
                    MultiSelectItemAnimator.this.dispatchFinishedWhenDone();
                }
            })).start();
        }

    }

    private final void endChangeAnimation(List infoList, RecyclerView.ViewHolder item) {
        IntProgression var10000 = RangesKt.reversed((IntProgression) CollectionsKt.getIndices((Collection) infoList));
        int i = var10000.getFirst();
        int var4 = var10000.getLast();
        int var5 = var10000.getStep();
        if (var5 > 0) {
            if (i > var4) {
                return;
            }
        } else if (i < var4) {
            return;
        }

        while (true) {
            MultiSelectItemAnimator.ChangeInfo changeInfo = (MultiSelectItemAnimator.ChangeInfo) infoList.get(i);
            if (this.endChangeAnimationIfNecessary(changeInfo, item) && changeInfo.getOldHolder() == null && changeInfo.getNewHolder() == null) {
                infoList.remove(changeInfo);
            }

            if (i == var4) {
                return;
            }

            i += var5;
        }
    }

    private final void endChangeAnimationIfNecessary(MultiSelectItemAnimator.ChangeInfo changeInfo) {
        RecyclerView.ViewHolder var10002;
        if (changeInfo.getOldHolder() != null) {
            var10002 = changeInfo.getOldHolder();
            if (var10002 == null) {
                Intrinsics.throwNpe();
            }

            this.endChangeAnimationIfNecessary(changeInfo, var10002);
        }

        if (changeInfo.getNewHolder() != null) {
            var10002 = changeInfo.getNewHolder();
            if (var10002 == null) {
                Intrinsics.throwNpe();
            }

            this.endChangeAnimationIfNecessary(changeInfo, var10002);
        }

    }

    private final boolean endChangeAnimationIfNecessary(MultiSelectItemAnimator.ChangeInfo changeInfo, RecyclerView.ViewHolder item) {
        boolean oldItem = false;
        if (changeInfo.getNewHolder() == item) {
            changeInfo.setNewHolder((RecyclerView.ViewHolder) null);
        } else {
            if (changeInfo.getOldHolder() != item) {
                return false;
            }

            changeInfo.setOldHolder((RecyclerView.ViewHolder) null);
            oldItem = true;
        }

        ViewCompat.setAlpha(item.itemView, 1.0F);
        ViewCompat.setTranslationX(item.itemView, 0.0F);
        ViewCompat.setTranslationY(item.itemView, 0.0F);
        this.dispatchChangeFinished(item, oldItem);
        return true;
    }

    public void endAnimation(@NotNull RecyclerView.ViewHolder item) {
        View view;
        int i;
        int var4;
        int var5;
        IntProgression var10000;
        label131:
        {
            Intrinsics.checkParameterIsNotNull(item, "item");
            view = item.itemView;
            ViewCompat.animate(view).cancel();
            var10000 = RangesKt.reversed((IntProgression) CollectionsKt.getIndices((Collection) this.mPendingMoves));
            i = var10000.getFirst();
            var4 = var10000.getLast();
            var5 = var10000.getStep();
            if (var5 > 0) {
                if (i > var4) {
                    break label131;
                }
            } else if (i < var4) {
                break label131;
            }

            while (true) {
                MultiSelectItemAnimator.MoveInfo additions = (MultiSelectItemAnimator.MoveInfo) this.mPendingMoves.get(i);
                if (additions.getHolder() == item) {
                    ViewCompat.setTranslationY(view, 0.0F);
                    ViewCompat.setTranslationX(view, 0.0F);
                    this.dispatchMoveFinished(item);
                    this.mPendingMoves.remove(i);
                }

                if (i == var4) {
                    break;
                }

                i += var5;
            }
        }

        this.endChangeAnimation(this.mPendingChanges, item);
        if (this.mPendingRemovals.remove(item)) {
            ViewCompat.setAlpha(view, 1.0F);
            this.dispatchRemoveFinished(item);
        }

        if (this.mPendingAdditions.remove(item)) {
            ViewCompat.setAlpha(view, 1.0F);
            this.dispatchAddFinished(item);
        }

        ArrayList additions1;
        label118:
        {
            var10000 = RangesKt.reversed((IntProgression) CollectionsKt.getIndices((Collection) this.mChangesList));
            i = var10000.getFirst();
            var4 = var10000.getLast();
            var5 = var10000.getStep();
            if (var5 > 0) {
                if (i > var4) {
                    break label118;
                }
            } else if (i < var4) {
                break label118;
            }

            while (true) {
                additions1 = (ArrayList) this.mChangesList.get(i);
                this.endChangeAnimation((List) additions1, item);
                if (additions1.isEmpty()) {
                    this.mChangesList.remove(i);
                }

                if (i == var4) {
                    break;
                }

                i += var5;
            }
        }

        label108:
        {
            var10000 = RangesKt.reversed((IntProgression) CollectionsKt.getIndices((Collection) this.mMovesList));
            i = var10000.getFirst();
            var4 = var10000.getLast();
            var5 = var10000.getStep();
            if (var5 > 0) {
                if (i > var4) {
                    break label108;
                }
            } else if (i < var4) {
                break label108;
            }

            while (true) {
                label105:
                {
                    additions1 = (ArrayList) this.mMovesList.get(i);
                    var10000 = RangesKt.reversed((IntProgression) CollectionsKt.getIndices((Collection) additions1));
                    int j = var10000.getFirst();
                    int var8 = var10000.getLast();
                    int var9 = var10000.getStep();
                    if (var9 > 0) {
                        if (j > var8) {
                            break label105;
                        }
                    } else if (j < var8) {
                        break label105;
                    }

                    while (true) {
                        MultiSelectItemAnimator.MoveInfo moveInfo = (MultiSelectItemAnimator.MoveInfo) additions1.get(j);
                        if (moveInfo.getHolder() == item) {
                            ViewCompat.setTranslationY(view, 0.0F);
                            ViewCompat.setTranslationX(view, 0.0F);
                            this.dispatchMoveFinished(item);
                            additions1.remove(j);
                            if (additions1.isEmpty()) {
                                this.mMovesList.remove(i);
                            }
                            break;
                        }

                        if (j == var8) {
                            break;
                        }

                        j += var9;
                    }
                }

                if (i == var4) {
                    break;
                }

                i += var5;
            }
        }

        label88:
        {
            var10000 = RangesKt.reversed((IntProgression) CollectionsKt.getIndices((Collection) this.mAdditionsList));
            i = var10000.getFirst();
            var4 = var10000.getLast();
            var5 = var10000.getStep();
            if (var5 > 0) {
                if (i > var4) {
                    break label88;
                }
            } else if (i < var4) {
                break label88;
            }

            while (true) {
                additions1 = (ArrayList) this.mAdditionsList.get(i);
                if (additions1.remove(item)) {
                    ViewCompat.setAlpha(view, 1.0F);
                    this.dispatchAddFinished(item);
                    if (additions1.isEmpty()) {
                        this.mAdditionsList.remove(i);
                    }
                }

                if (i == var4) {
                    break;
                }

                i += var5;
            }
        }

        if (this.mRemoveAnimations.remove(item) && Companion.getDEBUG()) {
            throw (Throwable) (new IllegalStateException("after animation is cancelled, item should not be in mRemoveAnimations list"));
        } else if (this.mAddAnimations.remove(item) && Companion.getDEBUG()) {
            throw (Throwable) (new IllegalStateException("after animation is cancelled, item should not be in mAddAnimations list"));
        } else if (this.mChangeAnimations.remove(item) && Companion.getDEBUG()) {
            throw (Throwable) (new IllegalStateException("after animation is cancelled, item should not be in mChangeAnimations list"));
        } else if (this.mMoveAnimations.remove(item) && Companion.getDEBUG()) {
            throw (Throwable) (new IllegalStateException("after animation is cancelled, item should not be in mMoveAnimations list"));
        } else {
            this.dispatchFinishedWhenDone();
        }
    }

    private final void resetAnimation(RecyclerView.ViewHolder holder) {
        AnimatorCompatHelper.clearInterpolator(holder.itemView);
        this.endAnimation(holder);
    }

    private final void dispatchFinishedWhenDone() {
        if (!this.isRunning()) {
            this.dispatchAnimationsFinished();
        }

    }

    public void endAnimations() {
        int count = this.mPendingMoves.size();
        int listCount = count - 1;
        byte i = 0;
        View changes;
        if (listCount >= i) {
            while (true) {
                MultiSelectItemAnimator.MoveInfo item = (MultiSelectItemAnimator.MoveInfo) this.mPendingMoves.get(listCount);
                changes = item.getHolder().itemView;
                ViewCompat.setTranslationY(changes, 0.0F);
                ViewCompat.setTranslationX(changes, 0.0F);
                this.dispatchMoveFinished(item.getHolder());
                this.mPendingMoves.remove(listCount);
                if (listCount == i) {
                    break;
                }

                --listCount;
            }
        }

        count = this.mPendingRemovals.size();
        listCount = count - 1;
        i = 0;
        RecyclerView.ViewHolder var11;
        if (listCount >= i) {
            while (true) {
                var11 = (RecyclerView.ViewHolder) this.mPendingRemovals.get(listCount);
                this.dispatchRemoveFinished(var11);
                this.mPendingRemovals.remove(listCount);
                if (listCount == i) {
                    break;
                }

                --listCount;
            }
        }

        count = this.mPendingAdditions.size();
        listCount = count - 1;
        i = 0;
        if (listCount >= i) {
            while (true) {
                var11 = (RecyclerView.ViewHolder) this.mPendingAdditions.get(listCount);
                changes = var11.itemView;
                ViewCompat.setAlpha(changes, 1.0F);
                this.dispatchAddFinished(var11);
                this.mPendingAdditions.remove(listCount);
                if (listCount == i) {
                    break;
                }

                --listCount;
            }
        }

        count = this.mPendingChanges.size();
        listCount = count - 1;
        i = 0;
        if (listCount >= i) {
            while (true) {
                this.endChangeAnimationIfNecessary((MultiSelectItemAnimator.ChangeInfo) this.mPendingChanges.get(listCount));
                if (listCount == i) {
                    break;
                }

                --listCount;
            }
        }

        this.mPendingChanges.clear();
        if (this.isRunning()) {
            listCount = this.mMovesList.size();
            int var12 = listCount - 1;
            byte var13 = 0;
            int j;
            byte var7;
            ArrayList var14;
            if (var12 >= var13) {
                while (true) {
                    var14 = (ArrayList) this.mMovesList.get(var12);
                    count = var14.size();
                    j = count - 1;
                    var7 = 0;
                    if (j >= var7) {
                        while (true) {
                            MultiSelectItemAnimator.MoveInfo item1 = (MultiSelectItemAnimator.MoveInfo) var14.get(j);
                            RecyclerView.ViewHolder view = item1.getHolder();
                            View view1 = view.itemView;
                            ViewCompat.setTranslationY(view1, 0.0F);
                            ViewCompat.setTranslationX(view1, 0.0F);
                            this.dispatchMoveFinished(item1.getHolder());
                            var14.remove(j);
                            if (var14.isEmpty()) {
                                this.mMovesList.remove(var14);
                            }

                            if (j == var7) {
                                break;
                            }

                            --j;
                        }
                    }

                    if (var12 == var13) {
                        break;
                    }

                    --var12;
                }
            }

            listCount = this.mAdditionsList.size();
            var12 = listCount - 1;
            var13 = 0;
            if (var12 >= var13) {
                while (true) {
                    var14 = (ArrayList) this.mAdditionsList.get(var12);
                    count = var14.size();
                    j = count - 1;
                    var7 = 0;
                    if (j >= var7) {
                        while (true) {
                            RecyclerView.ViewHolder var15 = (RecyclerView.ViewHolder) var14.get(j);
                            View var16 = var15.itemView;
                            ViewCompat.setAlpha(var16, 1.0F);
                            this.dispatchAddFinished(var15);
                            var14.remove(j);
                            if (var14.isEmpty()) {
                                this.mAdditionsList.remove(var14);
                            }

                            if (j == var7) {
                                break;
                            }

                            --j;
                        }
                    }

                    if (var12 == var13) {
                        break;
                    }

                    --var12;
                }
            }

            listCount = this.mChangesList.size();
            var12 = listCount - 1;
            var13 = 0;
            if (var12 >= var13) {
                while (true) {
                    var14 = (ArrayList) this.mChangesList.get(var12);
                    count = var14.size();
                    j = count - 1;
                    var7 = 0;
                    if (j >= var7) {
                        while (true) {
                            MultiSelectItemAnimator.ChangeInfo var10001 = (MultiSelectItemAnimator.ChangeInfo) var14.get(j);
                            Intrinsics.checkExpressionValueIsNotNull(var10001, "changes[j]");
                            this.endChangeAnimationIfNecessary(var10001);
                            if (var14.isEmpty()) {
                                this.mChangesList.remove(var14);
                            }

                            if (j == var7) {
                                break;
                            }

                            --j;
                        }
                    }

                    if (var12 == var13) {
                        break;
                    }

                    --var12;
                }
            }

            this.cancelAll$production_sources_for_module_multiselection(this.mRemoveAnimations);
            this.cancelAll$production_sources_for_module_multiselection(this.mMoveAnimations);
            this.cancelAll$production_sources_for_module_multiselection(this.mAddAnimations);
            this.cancelAll$production_sources_for_module_multiselection(this.mChangeAnimations);
            this.dispatchAnimationsFinished();
        }
    }

    public final void cancelAll$production_sources_for_module_multiselection(@NotNull List viewHolders) {
        Intrinsics.checkParameterIsNotNull(viewHolders, "viewHolders");
        IntProgression var10000 = RangesKt.reversed((IntProgression) CollectionsKt.getIndices((Collection) viewHolders));
        int i = var10000.getFirst();
        int var3 = var10000.getLast();
        int var4 = var10000.getStep();
        if (var4 > 0) {
            if (i > var3) {
                return;
            }
        } else if (i < var3) {
            return;
        }

        while (true) {
            ViewCompat.animate(((RecyclerView.ViewHolder) viewHolders.get(i)).itemView).cancel();
            if (i == var3) {
                return;
            }

            i += var4;
        }
    }

    public boolean canReuseUpdatedViewHolder(@NotNull RecyclerView.ViewHolder viewHolder, @NotNull List payloads) {
        Intrinsics.checkParameterIsNotNull(viewHolder, "viewHolder");
        Intrinsics.checkParameterIsNotNull(payloads, "payloads");
        return !payloads.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads);
    }

    static class MoveInfo {
        private final RecyclerView.ViewHolder holder;
        private final int fromX;
        private final int fromY;
        private final int toX;
        private final int toY;

        MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        public RecyclerView.ViewHolder getHolder() {
            return holder;
        }

        public int getFromX() {
            return fromX;
        }

        public int getFromY() {
            return fromY;
        }

        public int getToX() {
            return toX;
        }

        public int getToY() {
            return toY;
        }

        ;
    }

    static class ChangeInfo {
        private final RecyclerView.ViewHolder oldHolder;
        private final RecyclerView.ViewHolder newHolder;
        private final int fromX;
        private final int fromY;
        private final int toX;
        private final int toY;

        ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        public RecyclerView.ViewHolder getOldHolder() {
            return oldHolder;
        }

        public RecyclerView.ViewHolder getNewHolder() {
            return newHolder;
        }

        public int getFromX() {
            return fromX;
        }

        public int getFromY() {
            return fromY;
        }

        public int getToX() {
            return toX;
        }

        public int getToY() {
            return toY;
        }
    }

}
