
package master.inputmodule;

/**
 * Created by semaj on 17. 10. 20.
 */

public interface LineListener<T>{
    void handle(T data);
    void errorHandle(T data);
}
