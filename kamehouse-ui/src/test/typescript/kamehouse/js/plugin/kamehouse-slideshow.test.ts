/// <reference types="vitest/globals" />

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { installKameHouseMock, createDocumentMock } from '../../../helpers/kamehouse-mock';

describe('kamehouse-slideshow', () => {
  let slides: any[];
  let dots: any[];

  beforeEach(() => {
    vi.resetModules();
    slides = [{}, {}, {}];
    dots = [{}, {}, {}];
  });

  function install() {
    const doc: any = createDocumentMock();
    doc.getElementsByClassName = vi.fn((name: string) => {
      if (name === 'kamehouse-slideshow-slide') return slides;
      if (name === 'kamehouse-slideshow-dot') return dots;
      return [];
    });
    return installKameHouseMock({}, {
      window: { location: { search: '' } },
      document: doc
    });
  }

  it('loads the slideshow plugin', async () => {
    const mock = install();
    await import('@/kamehouse/js/plugin/kamehouse-slideshow');

    expect(mock.util.dom.append).toHaveBeenCalled();
    expect(mock.util.module.setModuleLoaded).toHaveBeenCalledWith('slideshow');
    expect(mock.plugin.slideshow).toBeDefined();
  });

  it('shows a specific slide', async () => {
    const mock = install();
    await import('@/kamehouse/js/plugin/kamehouse-slideshow');
    const slideshow = mock.plugin.slideshow;

    slideshow.setDotSlide(2);

    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(slides[1], 'block');
    expect(mock.util.dom.classListAdd).toHaveBeenCalledWith(dots[1], 'kamehouse-slideshow-dot-active');
  });

  it('wraps around when moving to the previous slide', async () => {
    const mock = install();
    await import('@/kamehouse/js/plugin/kamehouse-slideshow');
    const slideshow = mock.plugin.slideshow;

    slideshow.prevSlide();

    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(slides[slides.length - 1], 'block');
  });

  it('wraps around when moving past the last slide', async () => {
    const mock = install();
    await import('@/kamehouse/js/plugin/kamehouse-slideshow');
    const slideshow = mock.plugin.slideshow;

    slideshow.setDotSlide(3);
    slideshow.nextSlide();

    expect(mock.util.dom.setDisplay).toHaveBeenCalledWith(slides[0], 'block');
  });
});
