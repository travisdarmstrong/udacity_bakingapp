package com.randomrobotics.bakingapp.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.randomrobotics.bakingapp.R;
import com.randomrobotics.bakingapp.data.Recipe;
import com.randomrobotics.bakingapp.data.Step;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import timber.log.Timber;

/**
 * {@link Fragment} to display the {@link Recipe} {@link Step} details
 */
public class StepDetailFragment extends Fragment {
    private Recipe recipe;
    private int stepNum;
    private Step step;
    public static final String ARGUMENT_RECIPE = "arg-recipe";
    public static final String ARGUMENT_STEPNUMBER = "arg-stepnum";
    public static final String ARGUMENT_TWOPANE = "arg-twopane";
    @BindView(R.id.detail_instructions)
    TextView instructionsTxt;
    @Nullable
    @BindView(R.id.detail_nav_buttons)
    View navButtonsView;
    @Nullable
    @BindView(R.id.detail_nav_previous)
    TextView navButtonPrev;
    @Nullable
    @BindView(R.id.detail_nav_next)
    TextView navButtonNext;
    @BindView(R.id.detail_video)
    SimpleExoPlayerView videoPlayerView;
    @Nullable
    @BindView(R.id.detail_header)
    TextView headerTxt;
    @Nullable
    @BindView(R.id.landscape_scrollview)
    View landscapeScroll;
    private SimpleExoPlayer player;
    StepNavigationListener navigationListener;
    private boolean twoPaneDisplay;

    /**
     * Default Constructor
     */
    public StepDetailFragment() {
    }

    /**
     * Create a new instance of the {@link StepDetailFragment} using the supplied parameters
     *
     * @param recipe           The {@link Recipe} to display {@link Step} details from
     * @param stepNum          The selected {@link Step} number
     * @param isTwoPaneDisplay Whether or not this is being displayed as two-panes on a tablet display
     */
    public static StepDetailFragment newInstance(Recipe recipe, int stepNum, boolean isTwoPaneDisplay) {
        StepDetailFragment newFragment = new StepDetailFragment();
        // Set the arguments
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_RECIPE, recipe);
        args.putInt(ARGUMENT_STEPNUMBER, stepNum);
        args.putBoolean(ARGUMENT_TWOPANE, isTwoPaneDisplay);
        newFragment.setArguments(args);
        return newFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the arguments, they must contain Recipe and other data
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARGUMENT_RECIPE)) {
            recipe = args.getParcelable(ARGUMENT_RECIPE);
            stepNum = args.getInt(ARGUMENT_STEPNUMBER);
            step = recipe.getSteps().get(stepNum);
            twoPaneDisplay = args.getBoolean(ARGUMENT_TWOPANE);
        } else {
            Timber.e("Arguments did not contain a recipe");
            return;
        }
        Timber.d("New StepDetailFragment created for '%s' step %s", recipe.getName(), stepNum);
        // Verify the calling activity implements the StepNavigationListener interface
        // This is used for the Next/Previous buttons
        try {
            navigationListener = (StepNavigationListener) getContext();
        } catch (ClassCastException e) {
            throw new ClassCastException(getContext().toString() + "must implement StepNavigationListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stepdetail, container, false);
        // Bind the UI elements
        ButterKnife.bind(this, view);
        // Display the Recipe Step Details
        DisplayRecipeStep();
        return view;
    }

    /**
     * Display the {@link Recipe} {@link Step} details
     */
    private void DisplayRecipeStep() {
        // Set the main instructions text
        instructionsTxt.setText(step.getDescription());
        // If this is the first or last step, set visibility on the Prev/Next buttons
        if (stepNum == 0 && navButtonPrev != null) {
            navButtonPrev.setVisibility(View.INVISIBLE);
        }
        if (stepNum == recipe.getSteps().size() - 1 && navButtonNext != null) {
            navButtonNext.setVisibility(View.INVISIBLE);
        }
        // If tablet display, set visibility on the navigation buttons
        if (twoPaneDisplay) {
            if (navButtonsView != null)
                navButtonsView.setVisibility(View.GONE);
            if (headerTxt != null)
                headerTxt.setVisibility(View.GONE);
        }
        // If phone display, set the header visible and set text to the short description
        else {
            if (navButtonsView != null)
                navButtonsView.setVisibility(View.VISIBLE);
            if (headerTxt != null) {
                headerTxt.setVisibility(View.VISIBLE);
                headerTxt.setText(step.getShortDescription());
            }
        }
        // If the Step has a video URL, set up the Exoplayer display
        if (step.hasVideo()) {
            ShowVideo();
        } else {
            // If the Step does not have a Video URL, hide the Exoplayer display
            HideVideoPlayer();
        }
    }

    /**
     * Display the {@link Recipe} {@link Step} video in the {@link SimpleExoPlayerView}
     */
    private void ShowVideo() {
        // Make sure the Exoplayer View is visible
        videoPlayerView.setVisibility(View.VISIBLE);
        if (!twoPaneDisplay && landscapeScroll != null) {
            // Phone Display is in landscape mode. Set the video player size to fill the screen
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int height = metrics.heightPixels;
            int width = metrics.widthPixels;
            Timber.d("Height: %s\tWidth: %s", height, width);
            videoPlayerView.setMinimumHeight(height);
        }
        videoPlayerView.requestFocus();

        // Set up the view factory
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        player = ExoPlayerFactory.newSimpleInstance(getContext(),
                new DefaultTrackSelector(
                        new AdaptiveTrackSelection.Factory(
                                bandwidthMeter)));
        videoPlayerView.setPlayer(player);
        Uri videoUri = Uri.parse(step.getVidedoURL());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "BakingApp"), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(videoUri, dataSourceFactory, extractorsFactory, null, null);
        player.prepare(videoSource);
        // Do not auto-play the video
        player.setPlayWhenReady(false);
    }

    /**
     * Hide the {@link SimpleExoPlayerView} video display
     */
    private void HideVideoPlayer() {
        videoPlayerView.setVisibility(View.GONE);
    }


    /**
     * Handle click event on the Next navigation button (if displayed)
     * This is executed by the calling Activity through the {@link StepNavigationListener} interface
     */
    @Optional
    @OnClick(R.id.detail_nav_next)
    public void NextClick(View v) {
        Timber.d("Next button clicked");
        navigationListener.MoveToNext();
    }

    /**
     * Handle click event on the Previous navigation button (if displayed)
     * This is executed by the calling Activity through the {@link StepNavigationListener} interface
     */
    @Optional
    @OnClick(R.id.detail_nav_previous)
    public void PrevClick(View v) {
        Timber.d("Previous button clicked");
        navigationListener.MoveToPrevious();
    }

    /**
     * When this activity is paused, stop the player and release resources
     */
    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
            player.release();
        }
    }

    /**
     * Interface to handle click events on the Next and Previous buttons
     */
    public interface StepNavigationListener {
        /**
         * Move to the next {@link Step} in the {@link Recipe}
         */
        void MoveToNext();

        /**
         * Move to the previous {@link Step} in the {@link Recipe}
         */
        void MoveToPrevious();
    }

}
