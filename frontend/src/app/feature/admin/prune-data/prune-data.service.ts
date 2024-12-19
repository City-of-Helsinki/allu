// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable, BehaviorSubject } from 'rxjs';
// import { PruneDataItem } from './models/prude-data-item.model';

// @Injectable({
//   providedIn: 'root'
// })
// export class PruneDataService {
//   private endpoint = '/api/anonymizable';
//   private userEndpoint = '/api/anonymizable/user';
  
//   private currentTabSubject = new BehaviorSubject<string>('applications');
//   currentTab$ = this.currentTabSubject.asObservable();

//   constructor(private http: HttpClient) {}

//   setCurrentTab(tab: string): void {
//     this.currentTabSubject.next(tab);
//   }

//   getCurrentTab(): string {
//     return this.currentTabSubject.getValue();
//   }

//   fetchAllData(tab: string): Observable<PruneDataItem[]> {
//     this.setCurrentTab(tab);
//     const endpoint = tab === 'user_data' ? this.userEndpoint : this.endpoint;
//     return this.http.get<PruneDataItem[]>(endpoint);
//   }

//   deleteData(ids: number[]): Observable<void> {
//     const currentTab = this.getCurrentTab();
//     const endpoint = currentTab === 'user_data' ? this.userEndpoint : this.endpoint;
//     return this.http.post<void>(`${endpoint}/delete`, { ids });
//   }
// } 