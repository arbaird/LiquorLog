package com.austinbaird.liquorlog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class EditDrinkImage extends AppCompatActivity {

    GridView gridView;
    AppInfo appInfo;
    Boolean alreadyCreated;
    int drinkPos;
    MediaPlayer mp2;

    String descriptionList[] = {"Blue", "BlueIce", "BlueUmbrella", "BlueFruit", "BlueIceUmbrella", "BlueIceFruit", "BlueUmbrellaFruit", "BlueIceUmbrellaFruit",
            "DarkBrown", "DarkBrownIce", "DarkBrownUmbrella", "DarkBrownFruit", "DarkBrownIceUmbrella", "DarkBrownIceFruit", "DarkBrownUmbrellaFruit", "DarkBrownIceUmbrellaFruit",
            "Gold", "GoldIce", "GoldUmbrella", "GoldFruit", "GoldIceUmbrella", "GoldIceFruit", "GoldUmbrellaFruit", "GoldIceUmbrellaFruit",
            "Green", "GreenIce", "GreenUmbrella", "GreenFruit", "GreenIceUmbrella", "GreenIceFruit", "GreenUmbrellaFruit", "GreenIceUmbrellaFruit",
            "Red", "RedIce", "RedUmbrella", "RedFruit", "RedIceUmbrella", "RedIceFruit", "RedUmbrellaFruit", "RedIceUmbrellaFruit"};

    int drinksIcon[] = { R.drawable.bluesmall, R.drawable.blueicesmall, R.drawable.blueumbrellasmall, R.drawable.bluefruitsmall,
            R.drawable.blueiceumbrellasmall, R.drawable.blueicefruitsmall, R.drawable.blueumbrellafruitsmall, R.drawable.blueiceumbrellafruitsmall,
            R.drawable.darkbrownsmall, R.drawable.darkbrownicesmall, R.drawable.darkbrownumbrellasmall, R.drawable.darkbrownfruitsmall,
            R.drawable.darkbrowniceumbrellasmall, R.drawable.darkbrownicefruitsmall, R.drawable.darkbrownumbrellafruitsmall, R.drawable.darkbrowniceumbrellafruitsmall,
            R.drawable.goldsmall, R.drawable.goldicesmall, R.drawable.goldumbrellasmall, R.drawable.goldfruitsmall,
            R.drawable.goldiceumbrellasmall, R.drawable.goldicefruitsmall, R.drawable.goldumbrellafruitsmall, R.drawable.goldiceumbrellafruitsmall,
            R.drawable.greensmall, R.drawable.greenicesmall, R.drawable.greenumbrellasmall, R.drawable.greenfruitsmall,
            R.drawable.greeniceumbrellasmall, R.drawable.greenicefruitsmall, R.drawable.greenumbrellafruitsmall, R.drawable.greeniceumbrellafruitsmall,
            R.drawable.redsmall, R.drawable.redicesmall, R.drawable.redumbrellasmall, R.drawable.redfruitsmall,
            R.drawable.rediceumbrellasmall, R.drawable.redicefruitsmall, R.drawable.redumbrellafruitsmall, R.drawable.rediceumbrellafruitsmall};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_drink_image);
        appInfo = AppInfo.getInstance(this);

        alreadyCreated = getIntent().getExtras().getBoolean("alreadyCreated");
        drinkPos = getIntent().getExtras().getInt("drinkPosition");

        final MediaPlayer mp1 = MediaPlayer.create(this, R.raw.zeldamenuequip);
        mp2 = MediaPlayer.create(this, R.raw.zeldaimageclose);

        gridView = (GridView) findViewById(R.id.gridView);
        GridAdapter adapter = new GridAdapter(EditDrinkImage.this, drinksIcon, descriptionList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mp1.start();

                Intent intent = new Intent(EditDrinkImage.this, EditDrinkActivity.class);
                //put imageId in extras to update the image in edit drink activity
                intent.putExtra("imageId", drinksIcon[position]);
                //return the alreadyCreated boolean and drinkPos for edit drink activty to display
                //same drink it was displaying before entering edit image activity
                intent.putExtra("alreadyCreated", alreadyCreated);
                intent.putExtra("drinkPosition", drinkPos);
                appInfo.drinkToEdit.setImageID(drinksIcon[position]);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        mp2.start();
        super.onBackPressed();
    }

}
