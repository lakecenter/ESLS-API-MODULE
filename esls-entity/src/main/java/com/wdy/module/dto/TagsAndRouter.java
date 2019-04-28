package com.wdy.module.dto;

import com.wdy.module.entity.Router;
import com.wdy.module.entity.Tag;
import lombok.Data;

import java.util.ArrayList;

@Data
public class TagsAndRouter {
    private ArrayList<Tag> tags;
    private Router router;
    public TagsAndRouter(Router router){
        tags = new ArrayList();
        this.router = router;
    }
    public TagsAndRouter(ArrayList tags,Router router){
        this.tags = tags;
        this.router = router;
    }
    public void addTag(Tag tag){
        tags.add(tag);
    }
    @Override
    public String toString(){
        return String.valueOf(router.getId());
    }
    @Override
    public boolean equals(Object obj) {
        //比较是否是 TagsAndRouter 类对象
        if (!(obj instanceof TagsAndRouter)) {
            return false;
        }
        TagsAndRouter o = (TagsAndRouter)obj;
        return this.router.getId()==(o.router.getId());
    }
}
