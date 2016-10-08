package ru.example.sic.my_ads.fragments.support;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.activity.MainActivity;
import ru.example.sic.my_ads.fragments.main.catalog.CatalogFragment;
import ru.example.sic.my_ads.models.AdContent;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.RC_GET_PICTURE;
import static ru.example.sic.my_ads.Utils.scaleDown;

public class CreateAdFragment extends Fragment {
    public static final String TAG = "CreateAdFirstStepFragment";
    public String category;
    @BindView(R.id.choose_category)
    public TextView categoryText;
    AdContent content = new AdContent();
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
        startActivityForResult(intent, RC_GET_PICTURE);
    }

    @OnClick(R.id.done)
    void done() {
        if (!categoryText.getText().toString().equals("")) {
            if (!title.getText().toString().equals("")) {
                if (!description.getText().toString().equals("")) {
                    if (!cost.getText().toString().equals("")) {
                        double latitude = 0;
                        double longitude = 0;
                        try {
                            Geocoder geocoder = new Geocoder(getContext());
                            List<Address> addresses = geocoder.getFromLocationName(address.getText().toString(), 1);
                            if (addresses.size() > 0) {
                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        content.category = category;
                        content.adr = address.getText().toString();
                        content.title = title.getText().toString();
                        content.desc = description.getText().toString();
                        content.cost = Integer.parseInt(cost.getText().toString());
                        int i = 0;
                        switch (howLong.getCheckedRadioButtonId()) {
                            case R.id.one: {
                                i = 0;
                                break;
                            }
                            case R.id.three: {
                                i = 1;
                                break;
                            }
                            case R.id.ever: {
                                i = 2;
                                break;
                            }
                        }
                        content.duration = i;
                        content.currency = dropdownCurrency.getSelectedItem().toString();
                        content.latitude = latitude;
                        content.longitude = longitude;
                        content.pictures = pictures;
                        sendToDb(content);
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
        if (requestCode == RC_GET_PICTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap original = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                if (pictures.size() < 4) {
                    Bitmap resized = scaleDown(original, true);
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

    private void sendToDb(AdContent content) {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setTitle(getString(R.string.loading));
        progress.setMessage(getString(R.string.wait_loading));
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        progress.show();
        Observable.just(content)
                .map(new Func1<AdContent, Boolean>() {
                    @Override
                    public Boolean call(AdContent content) {
                        return Parse.Request.addAd(content);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean result) {
                        if (result) {
                            getActivity().finish();
                            getActivity().startActivity(new Intent(getContext(), MainActivity.class));
                        } else {
                            progress.dismiss();
                        }
                    }
                });
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
