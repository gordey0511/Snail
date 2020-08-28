package com.user.snail;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test extends AppCompatActivity {

    RecyclerView categoriesView;


    public static class MyViewModel extends ViewModel {

        // ...

        MutableLiveData<List<Subcategory>> data;

        public LiveData<List<Subcategory> > getData() {
            if (data == null) {
                data = new MutableLiveData<>();
                loadData();
            }
            return data;
        }

        private void loadData() {
            data.postValue(Test.getRandomData());
//            dataRepository.loadData(new Callback<String>() {
//                @Override
//                public void onLoad(String s) {
//                    data.postValue(s);
//                }
//            });
        }

    }


    static List<Subcategory> getRandomData() {
        final Random random = new Random();
        final ArrayList<Subcategory> subcategoryList = new ArrayList<>();

        for(int i2=0;i2<10;i2++){
            final int i=i2;
            subcategoryList.add(new Test.Subcategory(""));
        }
        return subcategoryList;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setTitle("Выберите профессию");

        MyViewModel model = new ViewModelProvider(this).get(MyViewModel.class);
//        MyViewModel model = ViewModelProviders.of(this).get(MyViewModel.class);

        LiveData<List<Subcategory>> data = model.getData();
        data.observe(this, new Observer<List<Subcategory>>() {
            @Override
            public void onChanged(@Nullable List<Subcategory> s) {
                Toast.makeText(getApplicationContext(),"Load Good",Toast.LENGTH_SHORT).show();
            }
        });


        categoriesView=findViewById(R.id.categories_list_theme_test);

        List<Subcategory> CircleCategories = Test.getRandomData();
        categoriesView.setHasFixedSize(true);
        categoriesView.setLayoutManager(new LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,
                false
        ));
        categoriesView.setAdapter(new SubcategoriesAdapter(CircleCategories));

    }


    static class Subcategory {
        String name;
        int all_taskTheme;


        Subcategory(String name)
        {
            this.name = name;
        }
    }




    static class SubcategoriesAdapter extends RecyclerView.Adapter<SubcategoriesAdapter.ViewHolder> {

        List<Subcategory> data;

        SubcategoriesAdapter(List<Subcategory> data) {
            this.data = data;
        }


        @Override
        public SubcategoriesAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subcategory_list_item_test, parent, false);
            SubcategoriesAdapter.ViewHolder viewHolder=new SubcategoriesAdapter.ViewHolder(view);
            viewHolder.buttonChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(parent.getContext(), TestForStudent.class);
                    intent.putExtra("id", TestForStudent.TestType.TYPE_OF_THE_FUTURE_PROFESSION.toString());
//                    intent.putExtra("ans1","Факультет математики,Факультет физики");
//                    intent.putExtra("ans2","Факультет физики");
                    parent.getContext().startActivity(intent);
                }
            });
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(final SubcategoriesAdapter.ViewHolder holder, final int position) {
            final Subcategory subcategory = data.get(position);

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView subcategoryName,subcategoryText,subcategoryLike;
            ProgressBar subcategoryProgressBar;
            Button buttonChoose;

            ViewHolder(View itemView) {
                super(itemView);
                buttonChoose=itemView.findViewById(R.id.buttonChoose);
//                subcategoryLike=itemView.findViewById(R.id.textViewLikeTheme);
//                subcategoryName = itemView.findViewById(R.id.textViewName);
//                subcategoryText = itemView.findViewById(R.id.textViewText);
//                subcategoryProgressBar = itemView.findViewById(R.id.progressBarTheme);
            }
        }
    }
}