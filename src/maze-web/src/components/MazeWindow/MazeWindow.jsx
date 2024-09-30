import { useEffect, useState } from 'react';
import './MazeWindow.css';

function MazeWindow({ maze, route }) {
  const [squares, setSquares] = useState([]);

  useEffect(() => {
    resetRoute();
    const mazeWindow = document.querySelector('.maze-window');
    if (mazeWindow && maze) {
      squares.forEach((item) => {
        if (item.children)
          item.children.forEach((child) => {
            console.log('here');
            child.classList.remove('.maze-window__route-item_border-bottom');
            child.classList.remove('.maze-window__route-item_border-right');
          })
      });
      const columns = `repeat(${maze.cols}, 1fr)`;
      const rows = `repeat(${maze.rows}, 1fr)`;
      mazeWindow.style.gridTemplateColumns = columns;
      mazeWindow.style.gridTemplateRows = rows;
      const newSquares = Array.from({ length: maze.rows * maze.cols }, (_, i) => {
        let k = Math.floor(i / maze.cols);
        let l = i % maze.cols;
        return (
          <div key={i} className={
            `maze__square 
            ${maze.onBottom[k][l] && 'maze__bottom-border'}
            ${maze.onRight[k][l] && 'maze__right-border'}
            `} x={k} y={l} >
            <div className='maze-window__route-item'></div>
            <div className='maze-window__route-item'></div>
            <div className='maze-window__route-item'></div>
            <div className='maze-window__route-item'></div>
          </div>
        )
      }
      );
      setSquares(newSquares);
    }
  }, [maze, squares, route]);

  useEffect(() => {
    resetRoute();
    if (maze && route.coords) {
      const children = document.querySelector('.maze-window').children;
      for (let i = 0; i < route.coords.length - 1; i++) {
        const currentX = route.coords[i][0];
        const currentY = route.coords[i][1];
        const nextX = route.coords[i + 1][0];
        const nextY = route.coords[i + 1][1];
        const current = children[currentY * maze.cols + currentX];
        const next = children[nextY * maze.cols + nextX];
        if(current){
          if (currentX > nextX) {
            current.children[0].classList.add('maze-window__route-item_border-bottom');
            next.children[1].classList.add('maze-window__route-item_border-bottom');
          } else if (currentX < nextX) {
            current.children[1].classList.add('maze-window__route-item_border-bottom');
            next.children[0].classList.add('maze-window__route-item_border-bottom');
          } else if (currentY > nextY) {
            current.children[0].classList.add('maze-window__route-item_border-right');
            next.children[2].classList.add('maze-window__route-item_border-right');
          } else if (currentY < nextY) {
            current.children[2].classList.add('maze-window__route-item_border-right');
            next.children[0].classList.add('maze-window__route-item_border-right');
          }
        }
      }
    }
  }, [squares, maze, route]);

  const resetRoute = () => {
    const children = document.querySelector('.maze-window').children;
    if (children) {
      for (let i = 0; i < children.length; i++) {
        children[i].children[0].classList.remove('maze-window__route-item_border-bottom');
        children[i].children[0].classList.remove('maze-window__route-item_border-right');
        children[i].children[1].classList.remove('maze-window__route-item_border-bottom');
        children[i].children[1].classList.remove('maze-window__route-item_border-right');
        children[i].children[2].classList.remove('maze-window__route-item_border-bottom');
        children[i].children[2].classList.remove('maze-window__route-item_border-right');
      }
    }
  }

  return (
    <div className='maze-window'>
      {squares}
    </div>
  );
}

export default MazeWindow;
