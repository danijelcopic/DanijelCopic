package rs.aleph.android.example13.activities.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.SQLException;

import rs.aleph.android.example13.R;
import rs.aleph.android.example13.activities.db.DatabaseHelper;
import rs.aleph.android.example13.activities.db.model.Attraction;
import rs.aleph.android.example13.activities.dialogs.AboutDialog;


import static rs.aleph.android.example13.activities.activity.FirstActivity.NOTIF_TOAST;


public class SecondActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SELECT_PICTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
    private static int NOTIFICATION_ID = 1;
    private int position = 0;
    private DatabaseHelper databaseHelper;
    private Attraction attraction;
    private SharedPreferences preferences;
    private AlertDialog dialogAlert;
    private Context context;


    // za izbor slike u dijalogu
    private ImageView preview;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        // TOOLBAR
        // aktiviranje toolbara 2 koji je drugaciji od onog iz prve aktivnosti
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_second);
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


        // hvatamo intent iz prve aktivnosti
        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");

        // na osnovu dobijene pozicije od intenta, pupunjavamo polja u drugoj aktivnosti
        try {

            attraction = getDatabaseHelper().getmAttractionDao().queryForId((int) position);

            String name = attraction.getmName();

            String description = attraction.getmDescription();

            String address = attraction.getmAddress();

            int phone = attraction.getmPhone();
            String stringPhone = Integer.toString(phone);

            String web = attraction.getmWeb();

            String time = attraction.getmTime();

            double price = attraction.getmPrice();
            String stringPrice = Double.toString(price);

            String comment = attraction.getmComment();


            // name
            TextView reName = (TextView) findViewById(R.id.attraction_name);
            reName.setText(name);

            // description
            TextView reDescription = (TextView) findViewById(R.id.attraction_description);
            reDescription.setText(description);

            // picture
            ImageView imageView = (ImageView) findViewById(R.id.picture);
            Uri mUri = Uri.parse(attraction.getmPictures());
            imageView.setImageURI(mUri);
            reset();

            // address
            TextView reAddress = (TextView) findViewById(R.id.attraction_address);
            reAddress.setText(address);

            // phone
            TextView rePhone = (TextView) findViewById(R.id.attraction_phone);
            rePhone.setText(stringPhone);

            // web
            TextView reWeb = (TextView) findViewById(R.id.attraction_web);
            reWeb.setText(web);

            // time
            TextView reTime = (TextView) findViewById(R.id.attraction_time);
            reTime.setText(time);

            // price
            TextView rePrice = (TextView) findViewById(R.id.attraction_price);
            rePrice.setText(stringPrice);

            // comment
            TextView reComment = (TextView) findViewById(R.id.attraction_comment);
            reComment.setText(comment);


        } catch (SQLException e) {
            e.printStackTrace();
        }



    }




    // MENU
    // prikaz menija
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // sta se desi kada kliknemo na stavke iz menija
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            // kada pritisnemo ikonicu za brisanje
            case R.id.action_delete:


                    // potvrda za brisanje ... otvara se dialod
                    AlertDialog.Builder ab = new AlertDialog.Builder(this);
                    ab.setMessage("Are you sure to delete?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

                break;

            // kada pritisnemo ikonicu za editovanje
            case R.id.action_edit:

                edit();

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    // Navigation Drawer ... u kom je stanju ... prikazan ili ne, pa da se vrati ili otvori
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

            Intent intent = new Intent(SecondActivity.this, SettingsActivity.class);  // saljemo intent Settings.class
            startActivity(intent);

        } else if (id == R.id.action_about) {

            if (dialogAlert == null) {
                dialogAlert = new AboutDialog(SecondActivity.this).prepareDialog(); // pozivamo prepareDialog() iz klase AboutDialog
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





    /**
     * EDIT podataka
     */

    // pozivamo pri izmeni podataka ....
    private void edit() {

        final Dialog dialog = new Dialog(SecondActivity.this); // aktiviramo dijalog
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


        final EditText attName = (EditText) dialog.findViewById(R.id.input_attraction_name);
        final EditText attDescription = (EditText) dialog.findViewById(R.id.input_attraction_description);
        final EditText attAddress = (EditText) dialog.findViewById(R.id.input_attraction_address);
        final EditText attPhone = (EditText) dialog.findViewById(R.id.input_attraction_phone);
        final EditText attWeb = (EditText) dialog.findViewById(R.id.input_attraction_web);
        final EditText attTime = (EditText) dialog.findViewById(R.id.input_attraction_time);
        final EditText attPrice = (EditText) dialog.findViewById(R.id.input_attraction_price);
        final EditText attComment = (EditText) dialog.findViewById(R.id.input_attraction_comment);


        // update podataka u dialog pre edita
        attName.setText(attraction.getmName());

        attDescription.setText(attraction.getmDescription());

        attAddress.setText(attraction.getmAddress());

        int phone = attraction.getmPhone();
        String stringPhone = Integer.toString(phone);
        attPhone.setText(stringPhone);

        attWeb.setText(attraction.getmWeb());

        attTime.setText(attraction.getmTime());

        double price = attraction.getmPhone();
        String stringPrice = Double.toString(price);
        attPrice.setText(stringPrice);

        attComment.setText(attraction.getmComment());


        // save
        Button save = (Button) dialog.findViewById(R.id.btn_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = attName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Name must be entered", Toast.LENGTH_SHORT).show();
                    return;
                }

                String description = attDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Description must be entered", Toast.LENGTH_SHORT).show();
                    return;
                }

                String address = attAddress.getText().toString();
                if (address.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Address must be entered", Toast.LENGTH_SHORT).show();
                    return;
                }

                int phone = 0;
                try {
                    phone = Integer.parseInt(attPhone.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(SecondActivity.this, "Phone must be number.", Toast.LENGTH_SHORT).show();
                    return;
                }


                String web = attWeb.getText().toString();
                if (web.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Web address must be entered", Toast.LENGTH_SHORT).show();
                    return;
                }


                String time = attTime.getText().toString();
                if (time.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Working time must be entered", Toast.LENGTH_SHORT).show();
                    return;
                }


                String comment = attComment.getText().toString();
                if (comment.isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Working time must be entered", Toast.LENGTH_SHORT).show();
                    return;
                }


                double price = 0;
                try {
                    price = Double.parseDouble(attPrice.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(SecondActivity.this, "Price must be number.", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (preview == null || imagePath == null) {
                    Toast.makeText(SecondActivity.this, "Picture must be choose", Toast.LENGTH_SHORT).show();
                    return;
                }


                attraction.setmName(name);
                attraction.setmDescription(description);
                attraction.setmPictures(imagePath);
                attraction.setmAddress(address);
                attraction.setmPhone(phone);
                attraction.setmWeb(web);
                attraction.setmComment(comment);
                attraction.setmPrice(price);


                try {

                    getDatabaseHelper().getmAttractionDao().update(attraction);

                    //provera podesavanja (toast ili notification bar)
                    boolean toast = preferences.getBoolean(NOTIF_TOAST, false);


                    if (toast) {
                        Toast.makeText(SecondActivity.this, "Attraction is updated", Toast.LENGTH_SHORT).show();
                    }

                    finish();  // ovo sam morao da bi se vratio na prvu aktivnost i osvezio bazu novim podacima

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();

            }
        });

        // cancel
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    // //provera podesavanja (toast ili notification bar) .... ovo pozivamo kada kliknemo na ikonicu u Tolbaru
    private void showMessage(String message) {

        boolean toast = preferences.getBoolean(NOTIF_TOAST, false);

        if (toast) {  // ako je aktivan toast prikazi ovo
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }



    // metoda za izbor slike pri editu
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


    //Metoda koja komunicira sa bazom podataka
    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    // pokretanje phone aplikacije
    public void call(View v) {

        int phone = attraction.getmPhone();
        String stringPhone = Integer.toString(phone);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", stringPhone, null));


        //provera permissiona
        if (ActivityCompat.checkSelfPermission(SecondActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SecondActivity.this,
                    android.Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(SecondActivity.this,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        }

        startActivity(callIntent);
    }




    // pokretanje web browsera
    public void web(View v) {

        String url = attraction.getmWeb();
        Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(url));

        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        startActivity(intent);
    }




    // pokretanje mape pri izboru adrese
    public void map(View v) {


        String url = attraction.getmAddress();
        Intent intent= new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:36.4103,44.3872"));
        startActivity(intent);
    }





    // picasso .... full image
    public void fullImage(View view) {
        final Dialog dialog = new Dialog(SecondActivity.this);
        dialog.setContentView(R.layout.fullimage);

        ImageView image = (ImageView) dialog.findViewById(R.id.full_image);
        Uri mUri = Uri.parse(attraction.getmPictures());

        Picasso.with(SecondActivity.this).load(mUri).into(image);

        Button close = (Button) dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    // dialog za potvrdu za brisanje
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        getDatabaseHelper().getmAttractionDao().delete(attraction);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                    //provera podesavanja
                    boolean toast = preferences.getBoolean(NOTIF_TOAST, false);

                    if (toast) {
                        Toast.makeText(SecondActivity.this, "Attraction is deleted", Toast.LENGTH_SHORT).show();
                    }


                    finish();

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.cancel();
                    break;
            }
        }
    };





    // reset url slike
    private void reset() {
        imagePath = "";
        preview = null;
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
