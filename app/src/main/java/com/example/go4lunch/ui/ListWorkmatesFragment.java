package com.example.go4lunch.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.CoreActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.model.Workmate;
import com.example.go4lunch.viewmodel.DemoViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListWorkmatesFragment extends Fragment {
        private DemoViewModel mDemoViewModel;

        private ListWorkmatesFragmentAdapter listWmateAdapter;

        private ArrayList<WorkmateItem> workmatesList;

        @BindView(R.id.ListWorkmatesRecycler)
        RecyclerView workmateRecycler;

        private FragmentManager mFragmentManager;

        public ListWorkmatesFragment() {
            // Required empty public constructor
        }

        public static ListWorkmatesFragment newInstance() {
            ListWorkmatesFragment fragment = new ListWorkmatesFragment();
            return fragment;
        }


        public void display(FragmentManager fragmentManager) {
            mFragmentManager = checkNotNull(fragmentManager);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            View view = inflater.inflate(R.layout.fragment_list_workmates, container,
                    false);
            ButterKnife.bind(this, view);

            /*Toolbar mToolbar = ((CoreActivity) getActivity()).findViewById(R.id.toolbar);
            // initialiser l'icone de la toolbar pour ce fragment
            mToolbar.setNavigationIcon(R.drawable.baseline_star);
            // définir l'action au clic sur le bouton de navigation de la toolbar
            mToolbar.setNavigationOnClickListener(v -> {
                //get parent activity
                DrawerLayout dl = ((CoreActivity)getActivity()).findViewById(R.id.coreDrawer);
                dl.open();
            });*/

            listWmateAdapter = new ListWorkmatesFragmentAdapter(new ArrayList<>());

            configureViewModel();

            configureWorkmatesRecyclerView();




            // Inflate the layout for this fragment
            return view;


        }

        @Override
        public void onResume() {
            //configureWorkmateItemList();
            super.onResume();
        }
    @Override
    public void onStart(){
        super.onStart();

        Toolbar mToolbar = ((CoreActivity) getActivity()).findViewById(R.id.toolbar);
        mToolbar.setTitle("Workmates");
        // initialise toolbar icon
        mToolbar.setNavigationIcon(R.drawable.setting_icon);
        // Defines navigation on click listener
        mToolbar.setNavigationOnClickListener(v -> {
            //get parent activity
            DrawerLayout dl = ((CoreActivity)getActivity()).findViewById(R.id.coreDrawer);
            dl.open();
        });

    }


        private void configureViewModel(){
            ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
            mDemoViewModel = new ViewModelProvider(this, viewModelFactory).get(DemoViewModel.class);
        }


    private void configureWorkmatesRecyclerView(){
        workmateRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        workmateRecycler.setAdapter(listWmateAdapter);
        configureWorkmateItemList();
        /*mDemoViewModel.getAllWorkmates().observe(this, arg ->{
            if (arg == null){
                Log.d("WORKMATES_FRAGMENT_LIST","No Workmates found in collection");
            }else{
                listWmateAdapter.setmWorkmates(arrayListWorkmateToWorkmateItem(arg));
            }
        });
        */

    }

    private void configureWorkmateItemList(){
        mDemoViewModel.getAllWorkmates().observe(this, arg ->{
            if (arg == null){
                Log.d("WORKMATES_FRAGMENT_LIST","No Workmates found in collection");
            }else{
                workmatesList = arrayListWorkmateToWorkmateItem(arg);
                mDemoViewModel.getAllLunch().observe(this, lunches ->{
                    for (int i=0; i<lunches.size(); i++){
                        for (int j =0; j<workmatesList.size(); j++){
                            if(lunches.get(i).getwMate().getId().equals(workmatesList.get(j).getId())){
                                workmatesList.get(j).setRestaurantForLunch(lunches.get(i).getRestaurant());
                            }
                        }
                    }
                    listWmateAdapter.setmWorkmates(workmatesList);
                });
            }
        });
    }

    public ArrayList<WorkmateItem> arrayListWorkmateToWorkmateItem(ArrayList<Workmate> workmateList){
        ArrayList<WorkmateItem> workmateItemArrayList= new ArrayList<>();
        for (int i = 0; i<workmateList.size(); i++){
                workmateItemArrayList.add(WorkmateItem.workmateToWorkmateItem(workmateList.get(i)));
        }
        return workmateItemArrayList;
    }


}
