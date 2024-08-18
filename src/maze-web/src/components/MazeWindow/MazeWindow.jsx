import { useEffect, useState } from 'react';
import './MazeWindow.css';

function MazeWindow({maze}) {
  const [squares, setSquares] = useState([]);
  
  useEffect(() => {
    const mazeWindow = document.querySelector('.maze-window');
    if (mazeWindow && maze) {
      const columns = `repeat(${maze.cols}, 1fr)`;
      const rows = `repeat(${maze.rows}, 1fr)`;
      mazeWindow.style.gridTemplateColumns = columns;
      mazeWindow.style.gridTemplateRows = rows;
      
      const newSquares = Array.from({ length: maze.rows * maze.cols }, (_, i) => (
        <div key={i} className={
          `maze__square 
          ${maze.onBottom[Math.floor(i/maze.cols)][i%maze.cols] && 'maze__bottom-border'}
          ${maze.onRight[Math.floor(i/maze.cols)][i%maze.cols] && 'maze__right-border'}
          `}></div>
      ));

      
      setSquares(newSquares);
    }
  }, [maze]);

  return (
    <div className='maze-window'>
      {squares}
    </div>
  );
}

export default MazeWindow;
