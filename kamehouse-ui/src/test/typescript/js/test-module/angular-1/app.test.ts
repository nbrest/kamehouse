/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('angular-1 app', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('configures the angular routes', async () => {
    let configCallback: any;
    const app: any = {
      controller: vi.fn(() => app),
      service: vi.fn(() => app),
      factory: vi.fn(() => app),
      config: vi.fn((cb: any) => { configCallback = cb; return app; })
    };
    (global as any).angular = { module: vi.fn(() => app), element: vi.fn() };
    installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });

    await import('@/js/test-module/angular-1/app');

    expect((global as any).angular.module).toHaveBeenCalledWith('myApp', ['ngRoute']);
    expect(configCallback).toBeTypeOf('function');

    const when = vi.fn().mockReturnThis();
    const otherwise = vi.fn().mockReturnThis();
    const routeProvider = { when, otherwise };
    const locationProvider = { hashPrefix: vi.fn() };
    configCallback(routeProvider, locationProvider);

    expect(when).toHaveBeenCalledWith('/', expect.any(Object));
    expect(when).toHaveBeenCalledWith('/dragonball/users', expect.any(Object));
    expect(when).toHaveBeenCalledWith('/400', expect.any(Object));
    expect(when).toHaveBeenCalledWith('/401', expect.any(Object));
    expect(when).toHaveBeenCalledWith('/403', expect.any(Object));
    expect(when).toHaveBeenCalledWith('/405', expect.any(Object));
    expect(when).toHaveBeenCalledWith('/409', expect.any(Object));
    expect(when).toHaveBeenCalledWith('/500', expect.any(Object));
    expect(otherwise).toHaveBeenCalledWith(expect.objectContaining({ templateUrl: '/kame-house/test-module/angular-1/view/404.html' }));
    expect(locationProvider.hashPrefix).toHaveBeenCalledWith('');
  });
});
