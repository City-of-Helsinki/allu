import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormControl} from '@angular/forms';
import {Subject} from 'rxjs';
import {MatLegacyOption as MatOption} from '@angular/material/legacy-core';
import {debounceTime, takeUntil} from 'rxjs/internal/operators';
import {Project} from '../../../model/project/project';

@Component({
  selector: 'project-select',
  templateUrl: './project-select.component.html',
  styleUrls: ['./project-select.component.scss']
})
export class ProjectSelectComponent implements OnInit, OnDestroy {
  @Input() matchingProjects: Project[];

  @Output() searchChange = new EventEmitter<string>(true);
  @Output() selectedChange = new EventEmitter<number>(true);

  searchControl = new UntypedFormControl();

  private destroy = new Subject<boolean>();

  constructor() {
  }

  ngOnInit(): void {
    this.searchControl.valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300)
    ).subscribe(term => this.searchChange.emit(term));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  add(option: MatOption): void {
    const project = option.value;
    this.selectedChange.emit(project.id);
    this.searchControl.reset();
  }

  displayName(project: Project): string {
    let displayName = '';
    if (project) {
      displayName = project.name
        ? `${project.identifier}: ${project.name || ''}`
        : project.identifier;
    }
    return displayName;
  }
}
