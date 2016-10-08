package ru.example.sic.my_ads.fragments.support;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import ru.example.sic.my_ads.Parse;
import ru.example.sic.my_ads.R;
import ru.example.sic.my_ads.adapters.ContactsEditAdapter;
import ru.example.sic.my_ads.adapters.ContactsViewAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static ru.example.sic.my_ads.Constants.RC_AVATAR;
import static ru.example.sic.my_ads.Parse.Constants.USER_AVATAR;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_COUNTRY_CODE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_IS_VISIBLE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_OF_TYPE;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_OF_USER;
import static ru.example.sic.my_ads.Parse.Constants.USER_CONTACTS_VALUE;
import static ru.example.sic.my_ads.Parse.Constants.USER_FIRST_NAME;
import static ru.example.sic.my_ads.Parse.Constants.USER_IS_PERSON;
import static ru.example.sic.my_ads.Parse.Constants.USER_LAST_NAME;
import static ru.example.sic.my_ads.Parse.Constants.USER_MIDDLE_NAME;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.person_type)
    RadioGroup isPerson;
    @BindView(R.id.fName)
    TextView fName;
    @BindView(R.id.mName)
    EditText mName;
    @BindView(R.id.lName)
    EditText lName;
    @BindView(R.id.contacts_list)
    RecyclerView contactsList;
    @BindView(R.id.add_contact)
    TextView addContact;
    @BindView(R.id.edit_contacts)
    TextView editContacts;
    private boolean contactsEditEnabled = true;
    private ContactsViewAdapter contactsListAdapterView;
    private ContactsEditAdapter contactsListAdapterEdit;

    @OnClick(R.id.setAvatar)
    void setAvatar() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, RC_AVATAR);
    }

    @OnClick(R.id.add_contact)
    void addContact() {
        if (Parse.Data.myContacts.size() < 6) {
            {
                ParseObject row = new ParseObject(USER_CONTACTS);
                row.put(USER_CONTACTS_OF_USER, ParseUser.getCurrentUser());
                row.put(USER_CONTACTS_COUNTRY_CODE, "");
                row.put(USER_CONTACTS_VALUE, "");
                row.put(USER_CONTACTS_OF_TYPE, 2);
                row.put(USER_CONTACTS_IS_VISIBLE, true);
                Parse.Data.myContacts.add(row);
                contactsListAdapterView.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.edit_contacts)
    void editContacts() {
        if (contactsEditEnabled) {
            addContact.setVisibility(View.INVISIBLE);
            contactsEditEnabled = false;
            editContacts.setText(getString(R.string.ready));
            contactsList.setAdapter(contactsListAdapterEdit);
        } else {
            addContact.setVisibility(View.VISIBLE);
            contactsEditEnabled = true;
            editContacts.setText(getString(R.string.edit_contacts));
            contactsList.setAdapter(contactsListAdapterView);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        setPersonalData();
        if(savedInstanceState==null&&Parse.Data.myContacts.size()==0) {
            getContacts(Parse.Data.currentUser);
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_AVATAR && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap img = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImage);
                avatar.setImageBitmap(img);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] dataAvatar = stream.toByteArray();
                ParseFile file = new ParseFile("photo.jpg", dataAvatar);
                Parse.Data.currentUser.put(USER_AVATAR, file);
                Parse.Data.currentUser.saveInBackground();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        Parse.Data.currentUser.put(USER_FIRST_NAME, fName.getText().toString());
        Parse.Data.currentUser.put(USER_MIDDLE_NAME, mName.getText().toString());
        Parse.Data.currentUser.put(USER_LAST_NAME, lName.getText().toString());
        Parse.Data.currentUser.put(USER_IS_PERSON, isPerson.getCheckedRadioButtonId() == R.id.person);
        Parse.Data.currentUser.saveInBackground();
        for (ParseObject contact : Parse.Data.myContacts) {
            contact.saveInBackground();
        }
        super.onPause();
    }

    protected void setPersonalData() {
        contactsList.setLayoutManager(new LinearLayoutManager(contactsList.getContext()));
        contactsListAdapterView = new ContactsViewAdapter(Parse.Data.myContacts);
        contactsListAdapterEdit = new ContactsEditAdapter(Parse.Data.myContacts);
        contactsList.setAdapter(contactsListAdapterView);
        if (Parse.Data.currentUser.getParseFile(Parse.Constants.USER_AVATAR) != null) {
            Glide.with(this)
                    .load(Parse.Data.currentUser.getParseFile(Parse.Constants.USER_AVATAR).getUrl())
                    .centerCrop()
                    .into(avatar);
        }
        if (Parse.Data.currentUser.getBoolean(Parse.Constants.USER_IS_PERSON)) {
            isPerson.check(R.id.person);
        } else {
            isPerson.check(R.id.organization);
        }
        fName.setText(Parse.Data.currentUser.getString(USER_FIRST_NAME));
        mName.setText(Parse.Data.currentUser.getString(USER_MIDDLE_NAME));
        lName.setText(Parse.Data.currentUser.getString(USER_LAST_NAME));
    }

    private void getContacts(final ParseObject owner) {
        Observable.just(owner != null)
                .map(new Func1<Boolean, ArrayList<ParseObject>>() {
                    @Override
                    public ArrayList<ParseObject> call(Boolean ownerExist) {
                        if (ownerExist) {
                            return Parse.Request.getContacts(owner, null);
                        }
                        return new ArrayList<>();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ParseObject>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final ArrayList<ParseObject> result) {
                        Parse.Data.myContacts.clear();
                        Parse.Data.myContacts.addAll(result);
                        contactsListAdapterView.notifyDataSetChanged();
                    }
                });
    }

}
