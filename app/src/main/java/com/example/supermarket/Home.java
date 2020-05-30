package com.example.supermarket;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private ImageSlider imageSlider;
    private FirebaseFirestore firebaseDatabase;
    private GridLayout categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        categoryList = findViewById(R.id.categoryList);

        final ArrayList<SlideModel> imageList = new ArrayList<SlideModel>();
        //imageList.add(new SlideModel("https://1.bp.blogspot.com/-GUZsgr8my50/XJUWOhyHyaI/AAAAAAAABUo/bljp3LCS3SUtj-judzlntiETt7G294WcgCLcBGAs/s1600/fox.jpg", "Foxes live wild in the city.", true));
        //imageList.add(new SlideModel("https://2.bp.blogspot.com/-CyLH9NnPoAo/XJUWK2UHiMI/AAAAAAAABUk/D8XMUIGhDbwEhC29dQb-7gfYb16GysaQgCLcBGAs/s1600/tiger.jpg"));
        //imageList.add(new SlideModel("https://3.bp.blogspot.com/-uJtCbNrBzEc/XJUWQPOSrfI/AAAAAAAABUs/ZlReSwpfI3Ack60629Rv0N8hSrPFHb3TACLcBGAs/s1600/elephant.jpg", "The population of elephants is decreasing in the world."));
        imageSlider = findViewById(R.id.image_slider);

        firebaseDatabase = FirebaseFirestore.getInstance();

        firebaseDatabase.collection("highlighted_products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<QueryDocumentSnapshot> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        imageList.add(new SlideModel(document.get("p_image").toString()));
                    }
                    imageSlider.setImageList(imageList, true);
                } else {
                    Log.d("MIKE", "Error getting documents: ", task.getException());
                }
            }
        });

        firebaseDatabase.collection("category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<QueryDocumentSnapshot> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document);
                        addCategory(document);
                        Log.d("MIKE", (String) document.get("c_type"));
                    }
                } else {
                    Log.d("MIKE", "Error getting documents: ", task.getException());
                }
            }
        });


        firebaseDatabase.collection("products")
                .whereEqualTo("c_id", "fruits_and_vegetables")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("MIKE", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("MIKE", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addCategory(QueryDocumentSnapshot document) {

        final View childView;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        childView = inflater.inflate(R.layout.category_list, null);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));

        param.width = 0;
        param.setMargins(10, 10, 10, 10);
        childView.setLayoutParams(param);
        final TextView textView = childView.findViewById(R.id.categoryType);
        textView.setText(document.get("c_type").toString());

        ImageView imageView = childView.findViewById(R.id.categoryImage);
        Glide.with(Home.this)
                .load(document.get("c_image"))
                .into(imageView);

        childView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), textView.getText(), Toast.LENGTH_LONG).show();
            }
        });

        categoryList.addView(childView);
        categoryList.requestLayout();
    }
}
