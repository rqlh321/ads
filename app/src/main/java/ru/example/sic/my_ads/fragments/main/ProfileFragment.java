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
import ru.example.sic.my_ads.models.User;

public class ProfileFragment extends Fragment {

    @BindView(R.id.person_type)
    public RadioGroup person;

    @BindView(R.id.name)
    public TextView name;

    @BindView(R.id.phone)
    public EditText phone;

    @BindView(R.id.email)
    public EditText email;

    @OnClick(R.id.save)
    public void save() {
        ParseUser.getCurrentUser().put(User.NAME, name.getText().toString());
        ParseUser.getCurrentUser().put(User.PHONE, phone.getText().toString());
        ParseUser.getCurrentUser().put(User.EMAIL, email.getText().toString());
        ParseUser.getCurrentUser().put(User.PERSON, person.getCheckedRadioButtonId() == R.id.person);
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
        if (ParseUser.getCurrentUser().getBoolean(User.PERSON)) {
            person.check(R.id.person);
        } else {
            person.check(R.id.organization);
        }
        name.setText(ParseUser.getCurrentUser().getString(User.NAME));
        phone.setText(ParseUser.getCurrentUser().getString(User.PHONE));
        email.setText(ParseUser.getCurrentUser().getString(User.EMAIL));
    }

}