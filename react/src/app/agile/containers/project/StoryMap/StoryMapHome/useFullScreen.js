import { useState, useEffect } from 'react';

function toFullScreen(dom = document.documentElement) {
  if (dom.requestFullscreen) {
    dom.requestFullscreen();
  } else if (dom.webkitRequestFullscreen) {
    dom.webkitRequestFullscreen();
  } else if (dom.mozRequestFullScreen) {
    dom.mozRequestFullScreen();
  } else {
    dom.msRequestFullscreen();
  }
}

function exitFullScreen() {
  if (document.exitFullscreen) {
    document.exitFullscreen();
  } else if (document.msExitFullscreen) {
    document.msExitFullscreen();
  } else if (document.mozCancelFullScreen) {
    document.mozCancelFullScreen();
  } else if (document.webkitExitFullscreen) {
    document.webkitExitFullscreen();
  }
}
function getCurrentFullScreen() {
  const isFullScreen = document.webkitFullscreenElement
    || document.mozFullScreenElement
    || document.msFullscreenElement;
  return Boolean(isFullScreen);
}

export default function useFullScreen(target, onFullScreenChange) {
  const [isFullScreen, setIsFullScreen] = useState(false);
  const handleChangeFullScreen = () => {    
    const currentFullScreen = getCurrentFullScreen();
    setIsFullScreen(currentFullScreen);
    if (onFullScreenChange) {
      onFullScreenChange(currentFullScreen);
    }
  };
  const toggleFullScreen = () => {
    const currentFullScreen = getCurrentFullScreen();
    if (currentFullScreen) {
      exitFullScreen();
    } else {
      toFullScreen(target);      
    }
  };
  useEffect(() => {
    document.addEventListener('fullscreenchange', handleChangeFullScreen);
    document.addEventListener('webkitfullscreenchange', handleChangeFullScreen);
    document.addEventListener('mozfullscreenchange', handleChangeFullScreen);
    document.addEventListener('MSFullscreenChange', handleChangeFullScreen);
    return function cleanup() {
      document.removeEventListener('fullscreenchange', handleChangeFullScreen);
      document.removeEventListener('webkitfullscreenchange', handleChangeFullScreen);
      document.removeEventListener('mozfullscreenchange', handleChangeFullScreen);
      document.removeEventListener('MSFullscreenChange', handleChangeFullScreen);
    };
  });
  return [isFullScreen, toggleFullScreen];
}
