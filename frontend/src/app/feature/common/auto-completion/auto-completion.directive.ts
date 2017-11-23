import {
  Directive, Input, Output, EventEmitter, HostListener, Renderer,
  ViewContainerRef, OnInit, OnDestroy, ComponentRef, ComponentFactoryResolver
} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {Observable} from 'rxjs/Observable';

import {AutoCompletionListComponent} from './auto-completion-list.component';
import {Some} from '../../../util/option';

const KEY_TAB = 'Tab';

@Directive({
  selector: '[autocompletion]'
})
export class AutoCompletionDirective implements OnInit, OnDestroy {

  // name needs to match selector to use it like [autocompletion]="searchFunction"
  // input for dropdown values
  @Input() autocompletion: Observable<Array<any>>;
  // Input for min length of search term which triggers search
  @Input() minTermLength = 3;
  // Name of the field which is used as id
  @Input() idField = 'id';
  // Name of the field which is shown in the dropdown
  @Input() nameField = 'name';
  // Sort function to override default sorting (by name field)
  @Input() sortBy: (a, b) => number;

  // Event for notifying search changes
  @Output() onSearchChange = new EventEmitter<string>();
  // Event for notifying item was selected
  @Output() onSelection = new EventEmitter<any>();

  private searchTerm = new Subject<string>();
  private listComponentRef: ComponentRef<AutoCompletionListComponent>;
  private inputEl: HTMLInputElement;  // input tag
  private dropdownEl: HTMLElement; // auto complete element
  private clickListener: Function;

  constructor(private resolver: ComponentFactoryResolver, private renderer: Renderer,
              public  viewContainerRef: ViewContainerRef) {
    this.inputEl = viewContainerRef.element.nativeElement;
  }

  ngOnInit(): void {
    this.autocompletion
      .filter(results => results.length > 0)
      .subscribe(searchResults => this.showDropdown());

    this.searchTerm
      .filter(term => term && term.length >= this.minTermLength)
      .debounceTime(300)
      .distinctUntilChanged()
      .subscribe(term => this.onSearchChange.emit(term));

    // disable default autcomplete from parent input
    this.inputEl.autocomplete = 'off';
    this.initDropdown();
  }

  ngOnDestroy(): void {
    this.clickListener();
  }

  @HostListener('keyup', ['$event']) onKeyUp(event: any) {
    this.searchTerm.next(event.target.value);
  }

  @HostListener('keydown', ['$event']) onKeyDown(event: any) {
    switch (event.code) {
      case KEY_TAB:
        this.hideDropdown(event);
        break;
      default:
        break;
    }
  }

  showDropdown() {
      this.dropdownEl = this.listComponentRef.location.nativeElement;
      this.dropdownEl.style.display = 'inline-block';
  }

  hideDropdown(event?: any): void {
    Some(this.dropdownEl).do(el => el.style.display = 'none');
  }

  private initDropdown() {
    const factory = this.resolver.resolveComponentFactory(AutoCompletionListComponent);
    this.listComponentRef = this.viewContainerRef.createComponent(factory);

    const component = this.listComponentRef.instance;
    component.entries = this.autocompletion;
    component.idField = this.idField;
    component.nameField = this.nameField;
    component.sortBy = this.sortBy;

    component.onSelection.subscribe(selection => {
      const name = selection[this.nameField] || selection;
      this.inputEl.value = name;
      this.onSelection.emit(selection);
    });

    // when somewhere else clicked, hide this autocomplete
    this.clickListener = this.renderer.listenGlobal('document', 'click', (event) => this.hideDropdown(event));

    this.hideDropdown();
  }
}
