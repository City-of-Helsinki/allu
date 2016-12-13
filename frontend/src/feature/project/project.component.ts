import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Project} from '../../model/project/project';

@Component({
  selector: 'project',
  template: require('./project.component.html'),
  styles: []
})
export class ProjectComponent implements OnInit {
  project: Project;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.route.data
      .map((data: {project: Project}) => data.project)
      .subscribe(project => {
        this.project = project;
      });
  }
}
