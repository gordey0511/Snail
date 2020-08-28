package com.user.snail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    Button login, register;

    FirebaseUser firebaseUser;

    FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        db=FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // check if user is null
        if(firebaseUser != null){
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseUser userVerify = FirebaseAuth.getInstance().getCurrentUser();

                    if (!userVerify.isEmailVerified()) {
                        Log.e("userInf",firebaseUser.getUid()+" "+firebaseUser.getEmail()+" "+firebaseUser.isEmailVerified());
                        Toast.makeText(getApplicationContext(),"Подтвердите почту",Toast.LENGTH_LONG).show();
                    }else{
//                        setProfession();
                        Intent intent = new Intent(StartActivity.this, TestForStudent.class);
                        intent.putExtra("id", TestForStudent.TestType.TYPE_OF_THE_FUTURE_PROFESSION.toString());
                        startActivity(intent);

                    }
                }
            });
        }


        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
    }


    public void setProfession() {
        final ArrayList arrayList=new ArrayList();
        arrayList.add("Математика и механика");
        arrayList.add("Компьютерные и информационные науки");
        arrayList.add("Физика и астрономия");
        arrayList.add("Химия");
        arrayList.add("Науки о Земле");
        arrayList.add("Биологические науки");
        arrayList.add("Архитектура");
        arrayList.add("Техника и технологии строительства");
        arrayList.add("Информатика и вычислительная техника");
        arrayList.add("Информационная безопасность");
        arrayList.add("Электроника, радиотехника и системы связи");
        arrayList.add("Фотоника, приборостроение, оптические и биотехнические системы и технологии");
        arrayList.add("Электро- и теплоэнергетика");
        arrayList.add("Ядерная энергетика и технологии");
        arrayList.add("Машиностроение");
        arrayList.add("Физико-технические науки и технологии");
        arrayList.add("Оружие и системы вооружения");
        arrayList.add("Химические технологии");
        arrayList.add("Химические технологии");
        arrayList.add("Промышленная экология и биотехнологии");
        arrayList.add("Техносферная безопасность и природообустройство");
        arrayList.add("Прикладная геология, горное дело, нефтегазовое дело и геодезия");
        arrayList.add("Технологии материалов");
        arrayList.add("Техника и технологии наземного транспорта");
        arrayList.add("Авиационная и ракетно-космическая техника");
        arrayList.add("Аэронавигация и эксплуатация авиационной и ракетно-космической техники");
        arrayList.add("Техника и технологии кораблестроения и водного транспорта");
        arrayList.add("Управление в технических системах");
        arrayList.add("Нанотехнологии и наноматериалы");
        arrayList.add("Технологии легкой промышленности");
        arrayList.add("Сестринское дело");
        arrayList.add("Сельское, лесное и рыбное хозяйство");
        arrayList.add("Ветеринария и зоотехния");
        arrayList.add("Психологические науки");
        arrayList.add("Экономика и управление");
        arrayList.add("Социология и социальная работа");
        arrayList.add("Юриспруденция");
        arrayList.add("Политические науки и регионоведение");
        arrayList.add("Средства массовой информации и информационно-библиотечное дело");
        arrayList.add("Сервис и туризм");
        arrayList.add("Образование и педагогические науки");
        arrayList.add("Языкознание и литературоведение");
        arrayList.add("История и археология");
        arrayList.add("Философия, этика и религиоведение");
        arrayList.add("Теология");
        arrayList.add("Физическая культура и спорт");
        arrayList.add("Искусствознание");
        arrayList.add("Культуроведение и социокультурные проекты");
        arrayList.add("Сценические искусства и литературное творчество");
        arrayList.add("Музыкальное искусство");
        arrayList.add("Изобразительное и прикладные виды искусств");
        final DocumentReference docRef = db.collection("test").document("professions");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        final Map<String, Object> inf = documentSnapshot.getData();
                        for(int i=0;i<arrayList.size();i++){
                            ArrayList arrayListNew=new ArrayList();
                            arrayListNew.add("Математика");
                            arrayListNew.add("Русский язык");
                            arrayListNew.add("Физика");

                            inf.put(arrayList.get(i).toString(),arrayListNew);

                            docRef.set(inf);
                        }
                    }
                }
            }
        });
    }
}
