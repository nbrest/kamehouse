package com.nicobrest.kamehouse.systemcommand.service;

import static org.mockito.Mockito.when;

import com.nicobrest.kamehouse.utils.PropertiesUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesUtils.class })
public class SystemCommandServiceTest {

  @Before
  public void before() {
    PowerMockito.mockStatic(PropertiesUtils.class);
  }
  
  @Test
  public void getSystemCommandsShutdownLinuxTest() { 
    when(PropertiesUtils.isWindowsHost()).thenReturn(true);
  
  }
}
