package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.Note;
import fi.hel.allu.servicecore.domain.NoteJson;

public class NoteMapper {
  public static NoteJson modelToJson(Note note) {
    NoteJson noteJson = new NoteJson();
    noteJson.setDescription(note.getDescription());
    return ApplicationExtensionMapper.modelToJson(note, noteJson);
  }

  public static Note jsonToModel(NoteJson json) {
    Note note = new Note();
    note.setDescription(json.getDescription());
    return ApplicationExtensionMapper.jsonToModel(json, note);
  }
}