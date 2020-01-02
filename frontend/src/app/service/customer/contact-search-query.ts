export interface ContactSearchQuery {
  name: string;
  active: boolean;
}

export const NAME_SEARCH_MIN_CHARS = 2;

export const ContactNameSearchMinChars = (term: string) => !!term && term.length >= NAME_SEARCH_MIN_CHARS;
