package ru.example.sic.my_ads.fragments.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.example.sic.my_ads.R;

public class ProfileFragment extends Fragment {
    private static final String NAME = "name";
    private static final String PERSON = "person";
    private static final String PHONE = "phone";
    private static final String EMAIL = "email";
    public static final String ADS = "ads";

    @BindView(R.id.person_type)
    RadioGroup person;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.phone)
    EditText phone;

    @BindView(R.id.email)
    EditText email;


    @OnClick(R.id.save)
    public void save() {
        ParseUser.getCurrentUser().put(NAME, name.getText().toString());
        ParseUser.getCurrentUser().put(PHONE, phone.getText().toString());
        ParseUser.getCurrentUser().put(EMAIL, email.getText().toString());
        ParseUser.getCurrentUser().put(PERSON, person.getCheckedRadioButtonId() == R.id.person);
        ParseUser.getCurrentUser().saveInBackground();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        setPersonalData();
        return view;
    }


    protected void setPersonalData() {
        if (ParseUser.getCurrentUser().getBoolean(PERSON)) {
            person.check(R.id.person);
        } else {
            person.check(R.id.organization);
        }
        name.setText(ParseUser.getCurrentUser().getString(NAME));
        phone.setText(ParseUser.getCurrentUser().getString(PHONE));
        email.setText(ParseUser.getCurrentUser().getString(EMAIL));
    }

}