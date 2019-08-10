package com.zubisoft.solutions.smartkitchen;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.zubisoft.solutions.smartkitchen.adapters.GalleryViewPagerAdapter;
import com.zubisoft.solutions.smartkitchen.model.Gallery;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ViewPager gallery_viewpager;
    private GalleryViewPagerAdapter gallery_adapter;
    private OnPageChangeListener pageChangeListener;
    private int currentPage = 0;
    private Runnable run;
    private TextView[] dots;
    private Handler handler;
    private boolean isDragged;
    private boolean isSettled;
    private GalleryViewPagerAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        pageChangeListener = new OnPageChangeListener(view);
        setupViewPager(view);

//        doFirestoreOperation();

        return view;
    }

    private void setupViewPager(View view) {
        gallery_viewpager = view.findViewById(R.id.gallery_viewpager);

        gallery_adapter = new GalleryViewPagerAdapter(getActivity(), fetchData());
        gallery_viewpager.setAdapter(gallery_adapter);
        gallery_viewpager.addOnPageChangeListener(pageChangeListener);
        //changeStatusBarColor();

        addDots(0, view);
        slideGallery();
    }

    private ArrayList<Gallery> fetchData() {

        ArrayList<Gallery> data = new ArrayList<>();

        Gallery gallery1 = new Gallery();
        gallery1.setTitle("Instant Messaging");
        gallery1.setDrawable(R.drawable.logo);

        Gallery gallery2 = new Gallery();
        gallery2.setTitle("Customer Care");
        gallery2.setDrawable(R.drawable.logo1);

        Gallery gallery3 = new Gallery();
        gallery3.setTitle("Super Android Assistance");
        gallery3.setDrawable(R.drawable.logo);

        data.add(gallery1);
        data.add(gallery2);
        data.add(gallery3);

        return data;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void slideGallery() {

        run = new Runnable() {
            @Override
            public void run() {
                if (gallery_adapter.getCount() - 1 == currentPage) {
                    currentPage = 0;
                } else {
                    currentPage += 1;
                }
                if (!isDragged) {
                    gallery_viewpager.setCurrentItem(currentPage, true);
                    isDragged = false;
                } else {

                }

                slideGallery();
            }

        };

        if (handler != null) {
            handler.removeCallbacks(run);
        }
        handler = new Handler();
        handler.postDelayed(run, 5000);
    }

    public void addDots(int pos, View v) {

        LinearLayout dots_layout = v.findViewById(R.id.dots_layout);

        dots = new TextView[fetchData().size()];
        dots_layout.removeAllViews();
        for (int i = 0; i < fetchData().size(); i++) {
            dots[i] = new TextView(v.getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(45);
//            dots[i].setTextColor(v.getResources().getColor(R.color.colorAccent));
            dots_layout.addView(dots[i]);
        }

        dots[pos].setTextColor(Color.YELLOW);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class OnPageChangeListener implements ViewPager.OnPageChangeListener {

        private View view;

        public OnPageChangeListener(View v) {
            this.view = v;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDots(position, view);
            currentPage = position;

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    isDragged = true;
//                    Toast.makeText(getApplicationContext(), "Scroll state dragging", Toast.LENGTH_SHORT).show();
                    break;

                case ViewPager.SCROLL_STATE_SETTLING:
//                        Toast.makeText(AppIntro1.this, "Scroll state settling", Toast.LENGTH_SHORT).show();
                    isSettled = true;
                    isDragged = false;
//                        slideIntro();
                    break;
                default:
                    isDragged = false;
                    isSettled = false;
                    break;
            }
        }
    }


}
