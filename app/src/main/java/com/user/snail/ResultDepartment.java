package com.user.snail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultDepartment extends AppCompatActivity {

    RecyclerView categoriesView;
    static Context context;
    static String ans1, ans2;
    static ArrayList<ArrayList> ans = new ArrayList();
    ArrayList Answer1;


    public static class MyViewModel extends ViewModel{
        MutableLiveData<List<Subcategory>> data;

        public LiveData<List<Subcategory>> getData(){
            if(data == null){
                data = new MutableLiveData<>();
                loadData();
            }
            return data;
        }

        private void loadData(){
            data.postValue(ResultDepartment.getRandomData());
        }
    }

    static List<ResultDepartment.Subcategory> getRandomData() {
        final ArrayList<ResultDepartment.Subcategory> subcategoryList = new ArrayList<>();

        for(int i2=0;i2<ans.size();i2++){
            final int i=i2;
            Log.e("getRandomDataFor",ans.get(i).get(0).toString());
            subcategoryList.add(new ResultDepartment.Subcategory(ans.get(i).get(0).toString(),ans.get(i).get(1).toString()));
        }

        Log.e("getRandomData",String.valueOf(ans.size()));
        return subcategoryList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Answer1=new ArrayList();

        context = getApplicationContext();

        setTitle("Выбор профессии");

        ans.clear();

        Log.e("createTest2","START");
        ans1 = getIntent().getStringExtra("ans1");
        if(ans1!=null) {
            Log.e("createTest2Ans1", ans1);
        }
        ans2 = getIntent().getStringExtra("ans2");
        if(ans2!=null) {
            Log.e("createTest2Ans2", ans2);
        }

        getAns2(ans1);
        sort();


        context=this;
        categoriesView=findViewById(R.id.categories_list_theme_test2);

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference documentReference3 = db.collection("test").document("directions");
        documentReference3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
              @Override
              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()) {
                      DocumentSnapshot documentSnapshot = task.getResult();
                      if (documentSnapshot.exists()) {
                          final Map<String, Object> m = documentSnapshot.getData();
                          for(int i=0;i<Answer1.size();i++){
                              String answer1=Answer1.get(i).toString();
                              Log.e("Answer1",answer1);
                              if(m.get(answer1)!=null&&((Map)m.get(answer1)).get(ans2)!=null){
                                  Log.e("transform", ((Map) m.get(answer1)).get(ans2).toString());
                                  transform(((Map) m.get(answer1)).get(ans2).toString());
                              }

                          }

                          ArrayList arrayList=new ArrayList();
                          arrayList.add("Факультет математики");
                          arrayList.add("Факультет математики");
                          ans.add(arrayList);
                      }else{
                          ArrayList arrayList=new ArrayList();
                          arrayList.add("Факультет математики");
                          arrayList.add("Факультет математики");
                          ans.add(arrayList);

                          ArrayList arrayList2=new ArrayList();
                          arrayList2.add("Науки о Земле");
                          arrayList2.add("Науки о Земле");
                          ans.add(arrayList);
                      }

                      MyViewModel model = new ViewModelProvider(ResultDepartment.this).get(MyViewModel.class);

                      LiveData<List<Subcategory>> data = model.getData();

                      data.observe(ResultDepartment.this, new Observer<List<Subcategory>>() {
                          @Override
                          public void onChanged(List<Subcategory> subcategories) {
                          }
                      });

                      List<ResultDepartment.Subcategory> CircleCategories = ResultDepartment.getRandomData();
                      categoriesView.setHasFixedSize(true);
                      categoriesView.setLayoutManager(new LinearLayoutManager(
                              context,
                              RecyclerView.VERTICAL,
                              false
                      ));
                      categoriesView.setAdapter(new ResultDepartment.SubcategoriesAdapter(CircleCategories));
                  }
              }
          });
    }

    public void transform(String s){

        Log.e("transform",s);

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.RUSSIAN)
                        .setTargetLanguage(TranslateLanguage.ENGLISH)
                        .build();
        final Translator translator =
                Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Log.e("translator", "downloaded lang  model");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Problem in translating the text entered",
                                        Toast.LENGTH_LONG).show();
                            }
                        });


        int l=0;

        for(int i = 0;i < s.length(); i++){
            if(s.charAt(i)==',') {
                String st = s.substring(l,i);

                Log.e("checkString",st);

                addProfession(st,translator);

                l=i+1;

            }
        }
        if(l!=s.length()){
            String st = s.substring(l,s.length());

            Log.e("checkString",st);

            addProfession(st,translator);
        }
    }


    public void getAns2(String s){
        int l=0;

        for(int i = 0;i < s.length(); i++){
            if(s.charAt(i)=='|') {
                String st = s.substring(l,i);

                Log.e("checkString",st);

                Answer1.add(st);

                l=i+1;

            }
        }
        if(l!=s.length()){
            String st = s.substring(l,s.length());
            Answer1.add(st);
            Log.e("checkString",st);
        }
    }

    public void getArray(String s){
        int l=0;

        for(int i = 0;i < s.length(); i++){
            if(s.charAt(i)==',') {
                String st = s.substring(l,i);

                Log.e("checkString",st);


                l=i+1;

            }
        }
        if(l!=s.length()){
            String st = s.substring(l,s.length());

            Log.e("checkString",st);

        }
    }

    private void addProfession(final String st,Translator translator) {
        ArrayList an=new ArrayList();
        an.add(st);
        an.add(st);
        ans.add(an);

        translator.translate(st)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
//                                an.clear();
//                                an.add(st);
//                                an.add(translatedText);
//                                ans.add(an);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
    }

    public void sort(){
        ArrayList<Integer> removed = new ArrayList<>();
        for (int i = 0;i < ans.size();i++){
            for(int j = i+1;j < ans.size();j++){
                Log.e("sortEqual",ans.get(i).get(0)+" "+ans.get(j).get(0));
                if(ans.get(i).get(0).toString().equals(ans.get(j).get(0).toString())){
                    Log.e("sortEqual","EQUAL "+String.valueOf(j));
                    removed.add(j);
                }
            }
        }

        if(removed.size() != 0) {
            for (int j = 0; j < removed.size(); j++) {
                Log.e("REMOVE",String.valueOf(removed.get(j))+" "+String.valueOf(ans.size()));
                ans.remove((int)removed.get(j));
                Log.e("REMOVE2",String.valueOf(removed.get(j))+" "+String.valueOf(ans.size()));
            }
            removed.clear();
        }
    }



    static class Subcategory {
        String name, image;
        int all_taskTheme;

        Subcategory(String name, String image)
        {
            this.name = name;
            this.image = name;
        }
    }




    static class SubcategoriesAdapter extends RecyclerView.Adapter<ResultDepartment.SubcategoriesAdapter.ViewHolder> {

        List<ResultDepartment.Subcategory> data;

        SubcategoriesAdapter(List<ResultDepartment.Subcategory> data) {
            this.data = data;
        }


        @Override
        public ResultDepartment.SubcategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.subcategory_list_item_test2, parent, false);
            return new ResultDepartment.SubcategoriesAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(final ResultDepartment.SubcategoriesAdapter.ViewHolder holder, final int position) {
            final ResultDepartment.Subcategory subcategory = data.get(position);

            holder.subcategoryName.setText(subcategory.name);
//            holder.subcategoryImage.setImageDrawable(Drawable.createFromPath(subcategory.image.toLowerCase()));
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent starter = new Intent(context, ResultEGE.class);
                    starter.putExtra("profession", subcategory.name);
                    starter.putExtra("professionFile", subcategory.image);
                    context.startActivity(starter);
                }
            });

            if(position==1){
                holder.subcategoryImage.setImageResource(R.drawable.worker);
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView subcategoryName;
            ImageView subcategoryImage;
            LinearLayout linearLayout;
            ViewHolder(View itemView) {
                super(itemView);
                linearLayout = itemView.findViewById(R.id.linearLayout);
                subcategoryName = itemView.findViewById(R.id.prof_name);
                subcategoryImage = itemView.findViewById(R.id.prof_icon);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}