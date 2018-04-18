import {BackendCodeSet} from '../backend-model/backend-codeset';
import {CodeSet} from '../../model/codeset/codeset';

export class CodeSetMapper {

  public static mapBackend(backendCodeSet: BackendCodeSet): CodeSet {
    return (backendCodeSet) ?
      new CodeSet(
        backendCodeSet.id,
        backendCodeSet.codeSetType,
        backendCodeSet.code,
        backendCodeSet.description,
        backendCodeSet.value) : undefined;
  }
}
