import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';
import { AuthService } from './auth/auth.service';

function authMock(loggedIn: boolean) {
  return {
    isLoggedIn: signal(loggedIn),
    userName: signal(loggedIn ? 'max' : ''),
    givenName: signal(loggedIn ? 'Max' : ''),
    roles: signal(loggedIn ? ['admin', 'user'] : []),
    login: vi.fn(),
    logout: vi.fn(),
  };
}

describe('App', () => {
  async function render(loggedIn: boolean) {
    const auth = authMock(loggedIn);
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([]), { provide: AuthService, useValue: auth }],
    }).compileComponents();
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    return { auth, element: fixture.nativeElement as HTMLElement };
  }

  it('shows login button when logged out', async () => {
    const { auth, element } = await render(false);
    const button = element.querySelector('header button') as HTMLButtonElement;
    expect(button.textContent).toContain('Login');
    button.click();
    expect(auth.login).toHaveBeenCalled();
  });

  it('shows user, roles and students link when logged in', async () => {
    const { element } = await render(true);
    expect(element.querySelector('.user')?.textContent).toContain('max (admin, user)');
    expect(element.querySelector('nav')?.textContent).toContain('Students');
  });
});
