package com.example.gasci.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.gasci.LesMagazinActivity;
import com.example.gasci.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.omarshehe.forminputkotlin.FormInputAutoComplete;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

import static com.example.gasci.LesMagazinActivity.RESTARTED_ACTIVITY;

public class LocationDialog extends DialogFragment {


    public static final String BUSINESS_LOOK_UP = "businessLookup";


    public static final String VILLE_SHARED_KEY = "villeInputText";
    public static final String COMMUNE_SHARED_KEY = "communeInputText";
    public static final String QUARTIER_SHARED_KEY = "quartierInputText";
    public static final String TAG = LocationDialog.class.getSimpleName();


    private SharedPreferences businessLookupSharedPref;

    private Context context;
    private ParallaxRecyclerAdapter<DocumentSnapshot> adapter;


    public LocationDialog(Context context, ParallaxRecyclerAdapter<DocumentSnapshot> adapter) {
        businessLookupSharedPref = context.getSharedPreferences(BUSINESS_LOOK_UP, Context.MODE_PRIVATE);
        this.context = context;
        this.adapter = adapter;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.location_layout, null);

        // sign in the user ...
        TextView villeTextView = view.findViewById(R.id.ville);
        FormInputAutoComplete communeAutoComplete = view.findViewById(R.id.commune);
        FormInputAutoComplete quartierAutoComplete = view.findViewById(R.id.quartier);

        String ville = villeTextView.getText().toString();
        String commune = communeAutoComplete.getValue();
        String quartier = communeAutoComplete.getValue();

        villeTextView.setText(businessLookupSharedPref.getString(VILLE_SHARED_KEY, ville));

        communeAutoComplete.setValue(businessLookupSharedPref.getString(VILLE_SHARED_KEY, commune));

        quartierAutoComplete.setValue(businessLookupSharedPref.getString(VILLE_SHARED_KEY, quartier));
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Rechercher", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {


                        SharedPreferences.Editor businesspref = businessLookupSharedPref.edit();
                        if (!ville.equals(villeTextView.getText().toString())) {
                            businesspref.putString(VILLE_SHARED_KEY, villeTextView.getText().toString());
                        } else if (!commune.equals(communeAutoComplete.getValue())) {
                            businesspref.putString(COMMUNE_SHARED_KEY, communeAutoComplete.getValue());
                        } else if (!quartier.equals(quartierAutoComplete.getValue())) {
                            businesspref.putString(QUARTIER_SHARED_KEY, quartierAutoComplete.getValue());
                        }
                        businesspref.apply();

                        Intent intent = new Intent(context, LesMagazinActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).setMessage("Choisir ton lieu et decouvre les magazin de gaz dans les alentours");


        return builder.create();

    }


}
