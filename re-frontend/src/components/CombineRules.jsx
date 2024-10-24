import { useState } from 'react';
import apiClient from '../api/axios'; // Make sure this is correctly set up for your API

const CombineRules = () => {
  const [ruleIds, setRuleIds] = useState('');
  const [ast, setAst] = useState(null);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    const idsArray = ruleIds.split(',').map((id) => id.trim()).filter((id) => id); // Split and trim

    try {
      const response = await apiClient.post('/combine', idsArray);
      setAst(response.data); // Assuming the API returns the AST in the response body
      setError(''); // Clear previous errors
    } catch (error) {
      console.error('Error combining rules', error);
      setError('Failed to combine rules. Please check the IDs and try again.');
      setAst(null); // Clear AST on error
    }
  };

  return (
    <div className="rule-engine-wrapper">
      
      <form className='rule-form' onSubmit={handleSubmit}>
        <div className='rule-input'>
          <label htmlFor="ruleIds">Enter Rule IDs (comma separated):</label>
          <input
            type="text"
            id="ruleIds"
            value={ruleIds}
            onChange={(e) => setRuleIds(e.target.value)}
            required
          />
        </div>
        <button className='rule-btn' type="submit">Combine</button>
      </form>

      {error && <div className="error-message">{error}</div>}

      {ast && (
        <div className="ast-result">
          <h2>Combined AST:</h2>
          <pre>{JSON.stringify(ast, null, 2)}</pre> {/* Format the AST for better readability */}
        </div>
      )}
    </div>
    
  );
};

export default CombineRules;
