"""Tests for the legacy test endpoint."""

from __future__ import annotations

import pytest
from fastapi import FastAPI
from fastapi.testclient import TestClient

from kamehouse_groot.api.v1 import test_endpoint

TEST_PATH = "/kame-house-groot/api/v1/test/test"


def _client() -> TestClient:
    app = FastAPI()
    app.include_router(test_endpoint.router)
    return TestClient(app)


def test_renders_test_page() -> None:
    client = _client()
    response = client.get(TEST_PATH, headers={"X-Test-Header": "test-value"})
    assert response.status_code == 200
    assert "print server info" in response.text
    assert "print all headers" in response.text
    assert "x-test-header: test-value" in response.text
    assert "testFunction: mada mada dane" in response.text
    assert "isLinuxHostExample()" in response.text
    assert "its a linux host. do linux host specific stuff" in response.text


def test_renders_non_linux_message(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setattr(test_endpoint.sys, "platform", "win32")
    client = _client()
    response = client.get(TEST_PATH)
    assert "its NOT linux host. do windows host specific stuff" in response.text
