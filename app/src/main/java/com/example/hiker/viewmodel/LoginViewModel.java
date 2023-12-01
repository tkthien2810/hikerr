package com.example.hiker.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hiker.repository.UserRepository;

public class LoginViewModel extends ViewModel {

    private UserRepository userRepository;

    private MutableLiveData<Boolean> loginResult = new MutableLiveData<>();

    public LoginViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Boolean> getLoginResult() {
        return loginResult;
    }

    public void loginUser(String username, String password) {
        userRepository.loginUser(username, password, new UserRepository.LoginCallback() {
            @Override
            public void onLoginResult(boolean success) {
                loginResult.setValue(success);
            }
        });
    }
}

