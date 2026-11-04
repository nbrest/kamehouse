import { defineConfig } from 'vitest/config';
import path from 'path';

export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    include: ['src/test/typescript/**/*.test.ts', 'src/test/typescript/**/*.spec.ts'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'text-summary', 'lcov'],
      include: ['src/main/typescript/**/*.ts'],
      exclude: ['src/main/typescript/**/*.d.ts'],
      all: true,
      thresholds: {
        lines: 80,
        functions: 60,
        branches: 60,
        statements: 80,
      },
    },
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src/main/typescript'),
    },
  },
});
