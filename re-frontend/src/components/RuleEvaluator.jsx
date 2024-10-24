import { useState } from 'react';
import apiClient from '../api/axios';
import { useParams } from 'react-router-dom';

const RuleEvaluator = () => {
  const { id } = useParams();
  const [data, setData] = useState({});
  const [result, setResult] = useState(null);

  const handleChange = (e) => {
    setData({
      ...data,
      [e.target.name]: e.target.value,
    });
  };

  const handleEvaluate = async () => {
    try {
      const response = await apiClient.post(`/${id}/evaluate`, data);
      setResult(response.data.result);
      alert('Evaluation successful!'); // User feedback
    } catch (error) {
      console.error('Error evaluating rule', error);
      alert('Evaluation failed. Please check your input and try again.'); // User feedback
    }
  };

  return (
    <div className='rule-engine-wrapper'>
      <h2>Evaluate Rule ID: {id}</h2>
      <div className='rule-input'>
        <label>Data (JSON format):</label>
        <input
          type="text"
          name="data"
          onChange={handleChange}
        />
      </div>
      <button className='rule-btn' onClick={handleEvaluate}>Evaluate</button>
      {result !== null && (
        <div>Evaluation Result: {result ? 'True' : 'False'}</div>
      )}
    </div>
  );
};

export default RuleEvaluator;
