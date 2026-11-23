import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { Student } from './student';
import { StudentService } from './student.service';

@Component({
  selector: 'app-students',
  templateUrl: './students.html',
  styleUrl: './students.css',
})
export class Students implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly studentService = inject(StudentService);

  protected readonly students = signal<Student[]>([]);
  protected readonly error = signal('');

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.studentService.getAll().subscribe({
      next: (students) => {
        this.students.set(students);
        this.error.set('');
      },
      error: (err: HttpErrorResponse) => this.showError(err),
    });
  }

  create(userid: string, firstname: string, lastname: string): void {
    this.studentService.create({ userid, firstname, lastname }).subscribe({
      next: () => this.load(),
      error: (err: HttpErrorResponse) => this.showError(err),
    });
  }

  delete(userid: string): void {
    this.studentService.delete(userid).subscribe({
      next: () => this.load(),
      error: (err: HttpErrorResponse) => this.showError(err),
    });
  }

  private showError(err: HttpErrorResponse): void {
    // 401 = nicht angemeldet, 403 = angemeldet, aber Rolle fehlt
    this.error.set(`Fehler ${err.status}: ${err.status === 403 ? 'keine Berechtigung (Rolle admin nötig)' : err.statusText}`);
  }
}
