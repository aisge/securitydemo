import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { environment } from '../../environments/environment';
import { Student } from './student';

/** Der Bearer-Token wird vom Interceptor von angular-oauth2-oidc angehängt. */
@Injectable({ providedIn: 'root' })
export class StudentService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.backendUrl}/students`;

  getAll() {
    return this.http.get<Student[]>(this.url);
  }

  create(student: Student) {
    return this.http.post<void>(this.url, student);
  }

  delete(userid: string) {
    return this.http.delete<void>(`${this.url}/${userid}`);
  }
}
