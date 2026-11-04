/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../helpers/kamehouse-mock';

describe('login', () => {
  beforeEach(() => {
    vi.resetModules();
  });

  it('loads the login manager without alert params', async () => {
    const mock = installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: createDocumentMock()
    });
    await import('@/js/login');

    expect(mock.logger.info).toHaveBeenCalled();
    expect(mock.util.banner.setRandomAllBanner).toHaveBeenCalled();
    expect(mock.util.dom.classListRemove).not.toHaveBeenCalled();
    expect(mock.extension.loginManager).toBeDefined();
  });

  it('shows all alert groups when params are present', async () => {
    const unauthorized = {};
    const error = {};
    const logout = {};
    const doc = createDocumentMock({
      'login-alert-group-unauthorized': unauthorized,
      'login-alert-group-error': error,
      'login-alert-group-logout': logout
    });
    const mock = installKameHouseMock({}, {
      window: { location: { search: '?unauthorizedPageAccess=true&error=true&logout=true' } },
      document: doc
    });
    await import('@/js/login');

    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(unauthorized, 'hidden-kh');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(error, 'hidden-kh');
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(logout, 'hidden-kh');
  });

  it('shows only the unauthorized alert group', async () => {
    const unauthorized = {};
    const doc = createDocumentMock({ 'login-alert-group-unauthorized': unauthorized });
    const mock = installKameHouseMock({}, {
      window: { location: { search: '?unauthorizedPageAccess=true' } },
      document: doc
    });
    await import('@/js/login');

    expect(mock.util.dom.classListRemove).toHaveBeenCalledTimes(1);
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(unauthorized, 'hidden-kh');
  });

  it('shows only the error alert group', async () => {
    const error = {};
    const doc = createDocumentMock({ 'login-alert-group-error': error });
    const mock = installKameHouseMock({}, {
      window: { location: { search: '?error=true' } },
      document: doc
    });
    await import('@/js/login');

    expect(mock.util.dom.classListRemove).toHaveBeenCalledTimes(1);
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(error, 'hidden-kh');
  });

  it('shows only the logout alert group', async () => {
    const logout = {};
    const doc = createDocumentMock({ 'login-alert-group-logout': logout });
    const mock = installKameHouseMock({}, {
      window: { location: { search: '?logout=true' } },
      document: doc
    });
    await import('@/js/login');

    expect(mock.util.dom.classListRemove).toHaveBeenCalledTimes(1);
    expect(mock.util.dom.classListRemove).toHaveBeenCalledWith(logout, 'hidden-kh');
  });
});
