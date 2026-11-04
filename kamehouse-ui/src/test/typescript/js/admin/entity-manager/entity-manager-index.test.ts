/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';

describe('admin-entity-manager-index', () => {
  beforeEach(() => {
    vi.resetModules();
    const obj: any = {};
    obj.ready = vi.fn((cb:any)=>{try{if(typeof cb==='function'){cb();}}catch(e){} return obj;});
    obj.on = vi.fn(()=>obj); obj.click = vi.fn(()=>obj); obj.each = vi.fn((fn:any)=>{try{if(typeof fn==='function'){fn.call(obj,0,obj);}}catch(e){} return obj;});
    (global as any).$ = vi.fn((sel:any)=>{ if(typeof sel==='function'){try{sel();}catch(e){} } return obj; });
    (global as any).jQuery = (global as any).$;
    (global as any).window = global;
    (global as any).document = { readyState: 'complete', addEventListener: vi.fn(), querySelectorAll: vi.fn(()=>[obj]), getElementById: vi.fn(()=>obj), createElement: vi.fn(()=>({ style:{}, innerHTML:'', appendChild:vi.fn() })) };
    (global as any).kameHouse = {
      logger: { info:vi.fn(), debug:vi.fn(), trace:vi.fn(), warn:vi.fn(), error:vi.fn(), logHttpResponse:vi.fn(), logApiError:vi.fn(), getRedText:(t:any)=>t },
      util: { banner: { setRandomAllBanner: vi.fn(), setRandomSanctuaryBanner: vi.fn() }, module: { waitForModules: vi.fn((cb:any)=>{try{if(typeof cb==='function'){cb();}}catch(e){} }), isModuleLoaded: vi.fn(()=>true) }, dom: {}, mobile:{exec:(cb:any)=>{try{if(typeof cb==='function'){cb();}}catch(e){}} }, collapsibleDiv:{resize:vi.fn()}, cookie:{}, string:{}, time:{} },
      core: { isEmpty: (v:any)=>v===undefined||v===null||v==='' },
      ready: vi.fn((cb:any)=>{try{if(typeof cb==='function'){cb();}}catch(e){} }),
      addPlugin: vi.fn(), extension:{}, plugin:{}, pluginLoader:{load:vi.fn()}
    };
  });

  it('executes admin entity-manager index', async () => {
    await import('../../../../../../src/main/typescript/js/admin/entity-manager/entity-manager-index');
    expect((global as any).kameHouse.util.banner.setRandomAllBanner).toHaveBeenCalled();
  });
});
