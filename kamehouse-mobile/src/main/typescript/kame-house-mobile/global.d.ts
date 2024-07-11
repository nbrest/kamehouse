export {};

declare global {
  const CryptoJS: any;

  interface Window {
    PERSISTENT: any;
    requestFileSystem: any;
  }
}