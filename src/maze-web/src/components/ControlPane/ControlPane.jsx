import './ControlPane.css';

function ControlPane({header, formName,buttons, fields}) {

  return (
    <div>
      <h3 className={'control-pane__form-header'} htmlFor={formName} >{header}</h3>
      <form className={'control-pane__form'} name={formName} >
        {
          fields && fields.map((item, index) =>{
            return (
            <label key={index} className={'control-pane__form-label'} >{item.name}:
              <input 
                className={'control-pane__form-input'} 
                name={item.name} 
                type='number' 
                min={item.min} 
                max={item.max} 
                defaultValue={item.default}
                step={item.step}
              />
            </label>)
          })
        }
        {
          buttons && buttons.map((item, index) => {
            return (
              <button key={index} type="submit" onClick={item.func}>
                {item.name}
              </button>
            );
          })
        }
      </form>
    </div>
  );
}

export default ControlPane;
