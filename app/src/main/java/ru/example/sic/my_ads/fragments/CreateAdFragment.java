package ru.example.sic.my_ads.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Constants;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.Utils;
import ru.example.sic.my_ads.fragments.main.CatalogFragment;
import ru.example.sic.my_ads.models.Ad;

public class CreateAdFragment extends Fragment {
    public static final String TAG = "CreateAdFragment";
    private Bitmap resizedPhoto;
    public String categoryId;
    @BindView(R.id.choose_category)
    public TextView categoryText;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.currency)
    Spinner dropdownCurrency;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.cost)
    EditText cost;
    @BindView(R.id.symbols_left)
    TextView symbolsLeft;
    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.removePhoto)
    ImageView removePhoto;
    @BindView(R.id.addPicture)
    TextView addPhoto;

    @OnClick(R.id.removePhoto)
    public void removePhoto() {
        photo.setVisibility(View.GONE);
        removePhoto.setVisibility(View.GONE);
        addPhoto.setVisibility(View.VISIBLE);
        resizedPhoto = null;
        photo.setImageBitmap(null);
    }

    @OnClick(R.id.choose_category)
    void chooseCategory() {
        getFragmentManager().beginTransaction()
                .hide(this)
                .add(R.id.support_container, new CatalogFragment(), TAG)
                .addToBackStack(null)
                .commit();
    }

    @OnClick(R.id.addPicture)
    void addPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.RC_GET_PICTURE);
    }

    @OnClick(R.id.done)
    void done() {
        if (!categoryText.getText().toString().isEmpty()) {
            if (!title.getText().toString().isEmpty()) {
                if (!description.getText().toString().isEmpty()) {
                    if (!cost.getText().toString().isEmpty()) {
                        if (resizedPhoto != null) {
                            List<Address> addresses = null;
                            try {
                                Geocoder geocoder = new Geocoder(getContext());
                                addresses = geocoder.getFromLocationName(address.getText().toString(), 1);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            if (addresses != null && addresses.size() > 0) {
                                double latitude = addresses.get(0).getLatitude();
                                double longitude = addresses.get(0).getLongitude();
                                final ParseObject row = new ParseObject(Ad.class.getSimpleName());
                                row.put(Ad.AUTHOR_ID, ParseUser.getCurrentUser().getObjectId());
                                row.put(Ad.VIEWS, 0);
                                row.put(Ad.RECOMMENDED, false);
                                row.put(Ad.COORDINATES, new ParseGeoPoint(latitude, longitude));
                                row.put(Ad.CURRENCY, dropdownCurrency.getSelectedItem().toString());
                                row.put(Ad.ADDRESS, address.getText().toString());
                                row.put(Ad.TITLE, title.getText().toString());
                                row.put(Ad.SUBCATEGORY, categoryId);
                                row.put(Ad.CONTENT, description.getText().toString());
                                row.put(Ad.COST, Integer.parseInt(cost.getText().toString()));

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                resizedPhoto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] data = stream.toByteArray();
                                ParseFile file = new ParseFile("photo.jpg", data);
                                row.put(Ad.PHOTO, file);
                                row.saveInBackground();

                                Toast.makeText(getActivity(), getString(R.string.create_ad_text), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            } else
                                Toast.makeText(getContext(), getString(R.string.bad_address), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(getContext(), getString(R.string.empty_cost), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getContext(), getString(R.string.empty_desc), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), getString(R.string.empty_title), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), getString(R.string.empty_category), Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_ad_first_part, container, false);
        ButterKnife.bind(this, view);

        ArrayAdapter<String> adapterCurrency = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.currencyType));
        dropdownCurrency.setAdapter(adapterCurrency);
        symbolsLeft.setText(getString(R.string.symbols_left, 800 - description.getText().length()));
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                symbolsLeft.setText(getString(R.string.symbols_left, 800 - description.getText().length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (savedInstanceState != null) {
            resizedPhoto = savedInstanceState.getParcelable("resizedPhoto");
            photo.setImageBitmap(resizedPhoto);
            categoryId = savedInstanceState.getString("category");
            categoryText.setText(savedInstanceState.getString("categoryText"));
        }
        if (resizedPhoto != null) {
            removePhoto.setVisibility(View.VISIBLE);
            addPhoto.setVisibility(View.GONE);
            photo.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RC_GET_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap original = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                resizedPhoto = Utils.scaleDown(original, true);
                photo.setImageBitmap(resizedPhoto);
                addPhoto.setVisibility(View.GONE);
                removePhoto.setVisibility(View.VISIBLE);
                photo.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("categoryText", categoryText.getText().toString());
        outState.putString("category", categoryId);
        outState.putParcelable("resizedPhoto", resizedPhoto);
    }

}
