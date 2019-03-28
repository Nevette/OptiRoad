package ad.optiroad;

import java.io.Serializable;
import java.util.ArrayList;

public class FavouritesRoutes implements Serializable {

    private Long id;
    private String title;
    private String content;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getSavedRouteContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }
}
