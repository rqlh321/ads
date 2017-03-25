package ru.example.sic.my_ads.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Constants;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.Utils;
import ru.example.sic.my_ads.activity.MainActivity;
import ru.example.sic.my_ads.fragments.main.catalog.CatalogFragment;
import ru.example.sic.my_ads.models.Ad;

public class CreateAdFragment extends Fragment {
    public static final String TAG = "CreateAdFirstStepFragment";
    public String category;
    @BindView(R.id.choose_category)
    public TextView categoryText;
    ArrayList<Bitmap> pictures = new ArrayList<>();
    GridViewAdapter gridAdapter;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.currency)
    Spinner dropdownCurrency;
    @BindView(R.id.how_long)
    RadioGroup howLong;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.cost)
    EditText cost;
    @BindView(R.id.symbols_left)
    TextView symbolsLeft;
    @BindView(R.id.gridView)
    GridView gridView;

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
                        final ProgressDialog progress = new ProgressDialog(getContext());
                        progress.setTitle(getString(R.string.loading));
                        progress.setMessage(getString(R.string.wait_loading));
                        progress.setCanceledOnTouchOutside(false);
                        progress.setCancelable(false);
                        progress.show();

                        double latitude = 0;
                        double longitude = 0;
                        try {
                            Geocoder geocoder = new Geocoder(getContext());
                            List<Address> addresses = geocoder.getFromLocationName(address.getText().toString(), 1);
                            if (addresses.size() > 0) {
                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        final ParseObject row = new ParseObject(Ad.class.getSimpleName());

                        row.put(Ad.AUTHOR_ID, ParseUser.getCurrentUser().getObjectId());
                        row.put(Ad.VIEWS, 0);
                        row.put(Ad.RECOMMENDED, false);
                        row.put(Ad.COORDINATES, new ParseGeoPoint(latitude, longitude));
                        row.put(Ad.CURRENCY, dropdownCurrency.getSelectedItem().toString());
                        row.put(Ad.ADDRESS, address.getText().toString());
                        row.put(Ad.TITLE, title.getText().toString());
                        row.put(Ad.SUBCATEGORY, category);
                        row.put(Ad.CONTENT, description.getText().toString());
                        row.put(Ad.COST, Integer.parseInt(cost.getText().toString()));

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        pictures.get(0).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] data = stream.toByteArray();
                        ParseFile file = new ParseFile("photo.jpg", data);
                        row.put(Ad.PHOTO, file);

                        row.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    progress.dismiss();
                                    if (e == null) {
                                        getActivity().finish();
                                        getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                                    }
                                }
                            }
                        });
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
        howLong.check(R.id.one);
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

        gridAdapter = new GridViewAdapter(getContext(), R.layout.grid_item_layout, pictures);
        gridView.setAdapter(gridAdapter);
        if (savedInstanceState != null) {
            category = savedInstanceState.getString("category");
            categoryText.setText(savedInstanceState.getString("categoryText"));
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
                if (pictures.size() < 4) {
                    Bitmap resized = Utils.scaleDown(original, true);
                    pictures.add(resized);
                    gridAdapter.notifyDataSetChanged();
                    gridView.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), getString(R.string.pictures_quot), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("categoryText", categoryText.getText().toString());
        outState.putString("category", category);
        super.onSaveInstanceState(outState);
    }

    class GridViewAdapter extends ArrayAdapter {
        private Context context;
        private int layoutResourceId;
        private ArrayList<Bitmap> data;

        public GridViewAdapter(Context context, int layoutResourceId, ArrayList<Bitmap> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            GridViewAdapter.ViewHolder holder;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new GridViewAdapter.ViewHolder();
                holder.image = (ImageView) row.findViewById(R.id.image);
                holder.delete = (ImageView) row.findViewById(R.id.delete);
                row.setTag(holder);
            } else {
                holder = (GridViewAdapter.ViewHolder) row.getTag();
            }

            Bitmap item = data.get(position);
            holder.image.setImageBitmap(item);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pictures.remove(position);
                    gridAdapter.notifyDataSetChanged();
                }
            });
            return row;
        }

        class ViewHolder {
            ImageView image;
            ImageView delete;
        }
    }
}
