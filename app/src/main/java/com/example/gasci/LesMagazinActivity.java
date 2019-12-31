package com.example.gasci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gasci.Dialogs.LocationDialog;
import com.example.gasci.Utils.QueryUtils;
import com.example.gasci.models.Magasin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.gasci.Dialogs.LocationDialog.BUSINESS_LOOK_UP;
import static com.example.gasci.Dialogs.LocationDialog.COMMUNE_SHARED_KEY;
import static com.example.gasci.Dialogs.LocationDialog.QUARTIER_SHARED_KEY;
import static com.example.gasci.Dialogs.LocationDialog.VILLE_SHARED_KEY;

public class LesMagazinActivity extends AppCompatActivity {


    public static final String TAG = LesMagazinActivity.class.getSimpleName();
    public static final String RESTARTED_ACTIVITY = "RestartedActivity";

    @BindView(R.id.rvRecyclerView)
    RecyclerView recyclerView;
    List<DocumentSnapshot> myContent = new ArrayList<>();

    private ParallaxRecyclerAdapter<DocumentSnapshot> adapter = new ParallaxRecyclerAdapter<DocumentSnapshot>(myContent) {
        @Override
        public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<DocumentSnapshot> adapter, int i) {


            Magasin magasin = myContent.get(i).toObject(Magasin.class);

            TextView prenomTxtView = viewHolder.itemView.findViewById(R.id.prenom_text_view);
            TextView nomMagazinTxtView = viewHolder.itemView.findViewById(R.id.nom_magazin_text_view);
            TextView villeTxtView = viewHolder.itemView.findViewById(R.id.ville_text_view);
            TextView communeTxtView = viewHolder.itemView.findViewById(R.id.commune_text_view);
            TextView quartierTxtView = viewHolder.itemView.findViewById(R.id.quartier_text_view);
            TextView numeroEditText = viewHolder.itemView.findViewById(R.id.numero_text_view);

            prenomTxtView.setText(magasin.getPrenom());
            nomMagazinTxtView.setText(magasin.getNomDeMagazin());
            villeTxtView.setText("Ville: " + magasin.getVille());
            communeTxtView.setText("Commune: " + magasin.getCommune());
            quartierTxtView.setText("Quartier: " + magasin.getQuartier());
            numeroEditText.setText("Numero: " + magasin.getNumero());


        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, final ParallaxRecyclerAdapter<DocumentSnapshot> adapter, int i) {
            // Here is where you inflate your row and pass it to the constructor of your ViewHolder
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            return new MyCustomViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.magazin_item, viewGroup, false));
        }

        @Override
        public int getItemCountImpl(ParallaxRecyclerAdapter<DocumentSnapshot> adapter) {
            // return the content of your array
            return myContent.size();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_les_magazin);

        ButterKnife.bind(this);


    }

    private class MyCustomViewHolder extends RecyclerView.ViewHolder {


        public MyCustomViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }


    @OnClick(R.id.location_item)
    void cherchGaz() {
        LocationDialog locationDialog = new LocationDialog(this, adapter);

        locationDialog.show(getSupportFragmentManager(), "");


    }


    @OnClick(R.id.reglage_item)
    void ouvreReglage() {
        Intent intent = new Intent(this, BusinessDetails.class);
        startActivity(intent);
    }

    /**
     * This method is used to make queries in the app for different business location
     *
     * @param ville
     * @param commune
     * @param quartier
     */

    public void initializeAdapter(String ville, String commune, String quartier) {
        Query query = QueryUtils.makeQuery(ville, commune, quartier);


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                myContent = task.getResult().getDocuments();

                if (task.isSuccessful() && myContent.size() > 0) {


                    adapter.setParallaxHeader(LayoutInflater.from(LesMagazinActivity.this).inflate(
                            R.layout.myparallaxview, null, false), recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(LesMagazinActivity.this));
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    if (!task.isSuccessful()) {
                        //TODO: screen for task was not successful most probably due to network issues
                        Log.w(TAG, "onComplete: task not successful ");

                    } else {
                        //TODO: Show empty list layout
                        Log.w(TAG, "Empty screen");

                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(BUSINESS_LOOK_UP, Context.MODE_PRIVATE);

        initializeAdapter(sharedPreferences.getString(VILLE_SHARED_KEY, getString(R.string.ville_default_value)),
                sharedPreferences.getString(COMMUNE_SHARED_KEY, getString(R.string.commune_default_value)),
                sharedPreferences.getString(QUARTIER_SHARED_KEY, getString(R.string.quartier_default_value)));

    }


}
