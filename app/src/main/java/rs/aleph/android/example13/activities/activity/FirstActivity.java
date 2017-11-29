package rs.aleph.android.example13.activities.activity;

import android.app.Dialog;

import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.io.IOException;
import java.sql.SQLException;

import java.util.ArrayList;

import java.util.List;

import rs.aleph.android.example13.R;
import rs.aleph.android.example13.activities.db.DatabaseHelper;
import rs.aleph.android.example13.activities.db.model.Attraction;
import rs.aleph.android.example13.activities.dialogs.AboutDialog;


public class FirstActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private static final int SELECT_PICTURE = 1;
    public static String NOTIF_TOAST = "pref_toast";

    private DatabaseHelper databaseHelper;
    private AlertDialog dialogAlert;
    private SharedPreferences preferences;


    // za izbor slike u dijalogu
    private ImageView preview;
    private String imagePath = null;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


        // TOOLBAR
        // aktiviranje toolbara
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_first);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);


        // Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // prikazivanje strelice u nazad u toolbaru ... mora se u manifestu definisati zavisnost parentActivityName
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.show();
        }


        // status podesavanja
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        //  ZA BAZU
        // ucitamo sve podatke iz baze u listu
        List<Attraction> attraction = new ArrayList<Attraction>();
        try {
            attraction = getDatabaseHelper().getmAttractionDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // u String izvucemo iz gornje liste  i sa adapterom posaljemo na View
        List<String> attName = new ArrayList<String>();
        for (Attraction i : attraction) {
            attName.add(i.getmName());
        }

        final ListView listView = (ListView) findViewById(R.id.list_first_activity); // definisemo u koji View saljemo podatke (listFirstActivity)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(FirstActivity.this, R.layout.list_item, attName);  // definisemo kako ce izgledati jedna stavka u View (list_item)
        listView.setAdapter(adapter);



        // sta se desi kada kliknemo na stavku iz liste
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Attraction attraction = (Attraction) listView.getItemAtPosition(position);
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                intent.putExtra("position", attraction.getmId());  // saljemo intent o poziciji
                startActivity(intent);

            }

        });

    }


    /**
     * MENU
     */

    // prikaz menija
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_first, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // sta se desi kada kliknemo na stavke iz menija
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.action_add: // otvara se dialog za upis u bazu


                final Dialog dialog = new Dialog(FirstActivity.this); // aktiviramo dijalog
                dialog.setContentView(R.layout.dialog_attraction);


                // pritisnemo btn choose da bi izabrali sliku
                Button choosebtn = (Button) dialog.findViewById(R.id.btn_choose);
                choosebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        preview = (ImageView) dialog.findViewById(R.id.preview_image);
                        selectPicture();
                    }
                });


                final EditText reName = (EditText) dialog.findViewById(R.id.input_attraction_name);
                final EditText reDescription = (EditText) dialog.findViewById(R.id.input_attraction_description);
                final EditText reAddress = (EditText) dialog.findViewById(R.id.input_attraction_address);
                final EditText rePhone = (EditText) dialog.findViewById(R.id.input_attraction_phone);
                final EditText reWeb = (EditText) dialog.findViewById(R.id.input_attraction_web);
                final EditText reTime = (EditText) dialog.findViewById(R.id.input_attraction_time);
                final EditText rePrice = (EditText) dialog.findViewById(R.id.input_realestate_price);
                final EditText reCommment = (EditText) dialog.findViewById(R.id.input_realestate_price);


                Button save = (Button) dialog.findViewById(R.id.btn_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = reName.getText().toString();
                        if (name.isEmpty()) {
                            Toast.makeText(FirstActivity.this, "Name must be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String description = reDescription.getText().toString();
                        if (description.isEmpty()) {
                            Toast.makeText(FirstActivity.this, "Description must be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String address = reAddress.getText().toString();
                        if (address.isEmpty()) {
                            Toast.makeText(FirstActivity.this, "Address muust be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int phone = 0;
                        try {
                            phone = Integer.parseInt(rePhone.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(FirstActivity.this, "Phone must be number.", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        String web = reWeb.getText().toString();
                        if (web.isEmpty()) {
                            Toast.makeText(FirstActivity.this, " Web address must be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        String time = reTime.getText().toString();
                        if (time.isEmpty()) {
                            Toast.makeText(FirstActivity.this, " Working time must be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }



                        double price = 0;
                        try {
                            price = Double.parseDouble(rePrice.getText().toString());
                        } catch (NumberFormatException e) {
                            Toast.makeText(FirstActivity.this, "Price must be number.", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        String comment = reCommment.getText().toString();
                        if (comment.isEmpty()) {
                            Toast.makeText(FirstActivity.this, " Comment must be entered", Toast.LENGTH_SHORT).show();
                            return;
                        }






                        if (preview == null || imagePath == null) {
                            Toast.makeText(FirstActivity.this, "Picture must be choose", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        Attraction attraction = new Attraction();
                        attraction.setmName(name);
                        attraction.setmDescription(description);
                        attraction.setmPictures(imagePath);
                        attraction.setmAddress(address);
                        attraction.setmPhone(phone);
                        attraction.setmWeb(web);
                        attraction.setmPrice(price);
                        attraction.setmComment(comment);



                        try {
                            getDatabaseHelper().getmAttractionDao().create(attraction);

                            //provera podesavanja
                            boolean toast = preferences.getBoolean(NOTIF_TOAST, false);

                            if (toast) {
                                Toast.makeText(FirstActivity.this, "New Attraction is added", Toast.LENGTH_SHORT).show();
                            }

                            reset();

                            refresh(); // osvezavanje baze

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();

                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //provera podesavanja
                        boolean toast = preferences.getBoolean(NOTIF_TOAST, false);


                        if (toast) {
                            Toast.makeText(FirstActivity.this, "New attraction is canceled", Toast.LENGTH_SHORT).show();
                        }

                        refresh(); // osvezavanje baze
                        dialog.dismiss();
                    }
                });

                dialog.show();

                break;

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * TABELE I BAZA
     */

    //Metoda koja komunicira sa bazom podataka
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    // refresh() prikazuje novi sadrzaj.Povucemo nov sadrzaj iz baze i popunimo listu
    private void refresh() {
        ListView listview = (ListView) findViewById(R.id.list_first_activity);
        if (listview != null) {
            ArrayAdapter<Attraction> adapter = (ArrayAdapter<Attraction>) listview.getAdapter();
            if (adapter != null) {
                adapter.clear();
                try {
                    List<Attraction> list = getDatabaseHelper().getmAttractionDao().queryForAll();
                    adapter.addAll(list);
                    adapter.notifyDataSetChanged();
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }



    private void reset(){
        imagePath = "";
        preview = null;
    }


    // metoda za izbor slike
    private void selectPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    // sistemska metoda koja se automatski poziva kada se aktivnost startuje u startActivityForResult rezimu   (slika)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    if (selectedImageUri != null) {
                        imagePath = selectedImageUri.toString();
                    }

                    if (preview != null) {
                        preview.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_list) {

        } else if (id == R.id.nav_settings) {

            Intent intent = new Intent(FirstActivity.this, SettingsActivity.class);  // saljemo intent Settings.class
            startActivity(intent);

        } else if (id == R.id.action_about) {

            if (dialogAlert == null) {
                dialogAlert = new AboutDialog(FirstActivity.this).prepareDialog(); // pozivamo prepareDialog() iz klase AboutDialog
            } else {
                if (dialogAlert.isShowing()) {
                    dialogAlert.dismiss();
                }

            }
            dialogAlert.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }










    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }


    // ovde refreshujemo bazu kada smo se vratili iz druge aktivnosti
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // nakon rada sa bazom podataka potrebno je obavezno osloboditi resurse!!!
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }


}