package master.inputmodule;

/**
 * Created by semaj on 17. 10. 20.
 */

public interface SimpleCallBack <T>{
    void onSuccess(T data);

    void onFailure();
}
