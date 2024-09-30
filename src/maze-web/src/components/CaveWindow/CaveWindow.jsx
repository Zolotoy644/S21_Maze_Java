import { useEffect, useState } from 'react';
import './CaveWindow.css';

function CaveWindow({cave}) {
  const [squares, setSquares] = useState([]);
  
  useEffect(() => {
    const caveWindow = document.querySelector('.cave-window');
    if (caveWindow && cave) {
      const columns = `repeat(${cave.cols}, 1fr)`;
      const rows = `repeat(${cave.rows}, 1fr)`;
      caveWindow.style.gridTemplateColumns = columns;
      caveWindow.style.gridTemplateRows = rows;
      const newSquares = Array.from({ length: cave.rows * cave.cols }, (_, i) => (
        <div 
          key={i} 
          className={
          `cave__square 
          ${cave.isAlive[Math.floor(i/cave.cols)][i%cave.cols] && 'cave__black'}
          `}
          ></div>
      ));
      setSquares(newSquares);
    }
  }, [cave]);

  return (
    <div className='cave-window'>
      {squares}
    </div>
  );
}

export default CaveWindow;
