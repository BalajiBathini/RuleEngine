import PropTypes from 'prop-types';
import { useState } from 'react';
import apiClient from '../api/axios';

const RuleForm = ({ onSuccess }) => {
  const [ruleString, setRuleString] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await apiClient.post('/create', ruleString);
      setRuleString('');
      // Optional chaining:
      onSuccess?.(response.data); 
      alert('Rule created successfully!'); // User feedback
    } catch (error) {
      console.error('Error creating rule', error);
      alert('Failed to create rule. Please try again.'); // User feedback
    }
  };

  return (
    <div className="rule-engine-wrapper">
      <form className='rule-form' onSubmit={handleSubmit}>
      <div className='rule-input'>

        <label>Rule:</label>
        <input
          type="text"
          value={ruleString}
          onChange={(e) => setRuleString(e.target.value)}
          required
        />
      </div>
      <button className='rule-btn' type="submit">Create Rule</button>
    </form>
    </div>
    
  );
};

RuleForm.propTypes = {
  onSuccess: PropTypes.func,
};



export default RuleForm;