package com.hbgtx.hola.callbacks;

import com.hbgtx.hola.model.EntityId;

public interface LoginCallback {
    void onSuccessfulLogin(EntityId userId);
    void onLoginFailure(String failureMessage);
}
