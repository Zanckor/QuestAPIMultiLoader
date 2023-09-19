package net.zanckor.questapi.api.filemanager.npc.entity_type.codec;

import net.zanckor.questapi.api.filemanager.FileAbstract;

import java.util.List;

@SuppressWarnings("unused")
public class EntityTypeDialog extends FileAbstract {
    private String id;
    private List<String> entity_type;
    private List<String> dialog_list;

    public List<String> getEntity_type() {
        return entity_type;
    }

    public List<String> getDialog_list() {
        return dialog_list;
    }

    public String getId() {
        return id;
    }
}
