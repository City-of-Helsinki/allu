alter table allu.contract add column signer text;
alter table allu.contract add column frame_agreement_exists boolean;
alter table allu.contract add column contract_as_attachment boolean;
alter table allu.contract rename column signed_contract to final_contract;
